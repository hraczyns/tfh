package com.hraczynski.trains.payment.payment;

import com.hraczynski.trains.email.EmailExtractor;
import com.hraczynski.trains.payment.Discount;
import com.hraczynski.trains.payment.price.PriceService;
import com.hraczynski.trains.reservations.ReservationEmailService;
import com.hraczynski.trains.exceptions.definitions.CannotCreatePaymentException;
import com.hraczynski.trains.exceptions.definitions.EntityNotFoundException;
import com.hraczynski.trains.passengers.Passenger;
import com.hraczynski.trains.passengers.unregistered.PassengerNotRegistered;
import com.hraczynski.trains.passengers.PassengerService;
import com.hraczynski.trains.passengers.discount.PassengerWithDiscountRequest;
import com.hraczynski.trains.payment.client.dto.CreatePaymentResponse;
import com.hraczynski.trains.payment.client.dto.SimplePassengerForPaymentSummaryDto;
import com.hraczynski.trains.reservations.*;
import com.hraczynski.trains.reservations.reservationscontent.ReservationContentService;
import com.hraczynski.trains.stoptime.StopTimeDto;
import com.hraczynski.trains.stoptime.StopTimeMapper;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static com.hraczynski.trains.reservations.ReservationStatus.COMPLETED;
import static com.hraczynski.trains.reservations.ReservationStatus.IN_PROGRESS;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private static final String SUCCEEDED = "payment_intent.succeeded";
    private static final String CREATED = "payment_intent.created";

    @Value("${stripe-secret-key}")
    private String secret;
    @Value("${stripe-webhook-secret}")
    private String stripeWebhookSecret;
    private final PriceService priceService;
    private final StopTimeMapper stopTimeMapper;
    private final PassengerService passengerService;
    private final ReservationService reservationService;
    private final PaymentRepository paymentRepository;
    private final ReservationContentService reservationContentService;
    private final ReservationEmailService reservationEmailService;
    private final EmailExtractor emailExtractor;

    @PostConstruct
    void initKey() {
        Stripe.apiKey = secret;
    }

    @Transactional
    public CreatePaymentResponse createPaymentIntent(ReservationRequest reservationRequest) {
        BigDecimal sumFromReservation = priceService.getSumFromReservation(reservationRequest);
        List<String> paymentMethodTypes = new ArrayList<>();
        paymentMethodTypes.add("card");
        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setAmount((long) (sumFromReservation.doubleValue() * 100L))
                        .setCurrency("pln")
                        .setReceiptEmail(extractMail(reservationRequest))
                        .addAllPaymentMethodType(paymentMethodTypes)
                        .build();

        PaymentIntent paymentIntent;
        try {
            paymentIntent = PaymentIntent.create(params);
        } catch (Exception e) {
            log.error("No stable internet connection. Cannot create payment form request.");
            throw new CannotCreatePaymentException("No stable internet connection. Cannot create payment form request.");
        }

        List<StopTimeDto> route = getRoute(reservationRequest.getReservedRoute());
        List<SimplePassengerForPaymentSummaryDto> passengers = getPassengersInfo(reservationRequest);
        String email = params.getReceiptEmail();

        Reservation reservation = reservationService.addReservation(reservationRequest, sumFromReservation);
        addPayment(reservation.getId(), reservation.getIdentifier(), paymentIntent.getId());
        return new CreatePaymentResponse(paymentIntent.getClientSecret(), sumFromReservation, route, passengers, email, reservation.getIdentifier());
    }

    private void addPayment(Long reservationId, String reservationIdentifier, String paymentIntentId) {
        Payment payment = new Payment(null, reservationId, paymentIntentId, reservationIdentifier, ReservationStatus.INIT, LocalDateTime.now(), LocalDateTime.now());
        log.info("Saving payment in init status.");
        paymentRepository.save(payment);
        log.info("Payment in init status has been saved.");
    }

    private String extractMail(ReservationRequest reservationRequest) {
        Iterator<PassengerWithDiscountRequest> iterator = reservationRequest
                .getIdPassengersWithDiscounts()
                .iterator();
        if (iterator.hasNext()) {
            PassengerWithDiscountRequest passengerWithDiscountRequest = iterator.next();
            Long passengerId = passengerWithDiscountRequest.getPassengerId();
            return passengerService.getById(passengerId).getEmail();
        }
        List<PassengerNotRegistered> passengerNotRegisteredList = reservationRequest.getPassengerNotRegisteredList();
        if (passengerNotRegisteredList != null && !passengerNotRegisteredList.isEmpty()) {
            PassengerNotRegistered passengerNotRegistered = passengerNotRegisteredList.get(0);
            return passengerNotRegistered.getEmail();
        }
        log.error("Email not found for reservation request.");
        throw new IllegalStateException("Email not found for reservation request.");
    }

    private List<SimplePassengerForPaymentSummaryDto> getPassengersInfo(ReservationRequest reservationRequest) {
        List<SimplePassengerForPaymentSummaryDto> passengers = reservationRequest.getIdPassengersWithDiscounts()
                .stream()
                .map(p -> {
                    Long passengerId = p.getPassengerId();
                    Passenger passenger = passengerService.getById(passengerId);
                    return new SimplePassengerForPaymentSummaryDto(passenger.getName() + " " + passenger.getSurname(), getDiscountName(p.getDiscountCode()));
                }).collect(Collectors.toList());

        passengers.addAll(reservationRequest.getPassengerNotRegisteredList()
                .stream()
                .map(p -> new SimplePassengerForPaymentSummaryDto(p.getName() + " " + p.getSurname(), getDiscountName(p.getDiscountCode())))
                .collect(Collectors.toList()));

        return passengers;
    }

    private String getDiscountName(String discountCode) {
        Discount discount = Discount.findByCode(discountCode);
        return discount != null ? discount.name() : "";
    }

    private List<StopTimeDto> getRoute(List<Long> reservedRoute) {
        List<StopTimeDto> stopTimes = stopTimeMapper.idsToDtos(reservedRoute);
        List<StopTimeDto> route = new ArrayList<>();
        route.add(stopTimes.get(0));
        route.add(stopTimes.get(stopTimes.size() - 1));
        return route;
    }

    @Transactional
    public void handleEvents(String sigStripe, String payload) {
        if (sigStripe == null || payload == null) {
            return;
        }

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigStripe, stripeWebhookSecret);
        } catch (SignatureVerificationException e) {
            return;
        }

        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject;
        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        } else {
            log.error("Deserialization error!");
            return;
        }
        PaymentIntent paymentIntent;
        try {
            paymentIntent = (PaymentIntent) stripeObject;
        } catch (ClassCastException ignored) {
            return;
        }
        String id = paymentIntent.getId();
        String status = paymentIntent.getStatus();
        Payment payment = paymentRepository.findByPaymentId(id);
        if (payment == null) {
            log.error("Payment (paymentId = {}) is not found!", id);
            return;
        }
        if (SUCCEEDED.equals(event.getType())) {
            log.info("PaymentIntent is paid successfully! id: {} , status: {}", id, status);
            updateStatusAndModificationDate(payment, COMPLETED);
            sendEmailWithTicket(payment);
        } else if (CREATED.equals(event.getType())) {
            log.info("PaymentIntent is created! id: {} , status: {}", id, status);
            updateStatusAndModificationDate(payment, IN_PROGRESS);
        } else {
            log.warn("Unhandled event type: {} ", event.getType());
        }
    }

    private void sendEmailWithTicket(Payment payment) {
        Reservation reservation = getReservation(payment);
        List<String> emails = emailExtractor.getEmails(reservation);
        File ticket = reservationContentService.getTicket(reservation);
        reservationEmailService.sendReservationEmailWithTicket(emails, reservation.getIdentifier(), ticket);
    }

    private void updateStatusAndModificationDate(Payment payment, ReservationStatus status) {
        payment.setStatus(status);
        payment.setUpdatedAt(LocalDateTime.now());
        Reservation reservation = getReservation(payment);
        reservation.setStatus(status);

        log.info("Updating payment to status {}", status.name());
        paymentRepository.save(payment);
        log.info("Payment status has been successfully updated");
        reservationService.updateStatus(reservation, status);
    }

    private Reservation getReservation(Payment payment) {
        Long reservationId = payment.getReservationId();
        return reservationService.getById(reservationId);
    }

    public Payment getPayment(String paymentContentId) {
        Payment payment = paymentRepository.findByPaymentId(paymentContentId);
        if (payment == null) {
            log.error("Cannot find Payment with id = {}", paymentContentId);
            throw new EntityNotFoundException(Payment.class, "id = " + paymentContentId);
        }
        return payment;
    }

    public ReservationShortResponse getReservationIdentifierByPaymentId(String paymentId) {
        log.info("Looking for reservation identifier by paymentId {}", paymentId);
        Payment payment = paymentRepository.findByPaymentId(paymentId);
        if (payment == null) {
            log.error("Cannot find Payment with id = {}", paymentId);
            throw new EntityNotFoundException(Payment.class, "paymentId=" + paymentId);
        }
        String reservationIdentifier = payment.getReservationIdentifier();
        Reservation reservation = reservationService.getByUniqueIdentifierInternalUsage(reservationIdentifier);
        List<String> emails = emailExtractor.getEmails(reservation);
        if (emails == null || emails.isEmpty()) {
            log.error("Cannot create short reservation response. No mails are found.");
            return new ReservationShortResponse();
        }
        return new ReservationShortResponse(reservationIdentifier, emails.get(0));

    }
}
