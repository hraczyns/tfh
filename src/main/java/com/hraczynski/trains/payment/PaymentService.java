package com.hraczynski.trains.payment;

import com.hraczynski.trains.exceptions.definitions.EntityNotFoundException;
import com.hraczynski.trains.passengers.Passenger;
import com.hraczynski.trains.passengers.PassengerNotRegistered;
import com.hraczynski.trains.passengers.PassengerService;
import com.hraczynski.trains.passengers.PassengerWithDiscount;
import com.hraczynski.trains.payment.client.dto.CreatePaymentResponse;
import com.hraczynski.trains.payment.client.dto.SimplePassengerForPaymentSummaryDto;
import com.hraczynski.trains.reservations.*;
import com.hraczynski.trains.stoptime.StopTimeDto;
import com.hraczynski.trains.stoptime.StopTimeMapper;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
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

    @PostConstruct
    void initKey() {
        Stripe.apiKey = secret;
    }

    @Transactional
    public CreatePaymentResponse createPaymentIntent(ReservationRequest reservationRequest) throws StripeException {

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

        PaymentIntent paymentIntent = PaymentIntent.create(params);

        List<StopTimeDto> route = getRoute(reservationRequest.getReservedRoute());
        List<SimplePassengerForPaymentSummaryDto> passengers = getPassengersInfo(reservationRequest);
        String email = params.getReceiptEmail();

        ReservationDto reservationDto = reservationService.addReservation(reservationRequest, sumFromReservation);
        addPayment(reservationDto.getId(), paymentIntent.getId());
        return new CreatePaymentResponse(paymentIntent.getClientSecret(), sumFromReservation, route, passengers, email, reservationDto.getIdentifier());
    }

    private void addPayment(Long reservationId, String paymentIntentId) {
        Payment payment = new Payment(null, reservationId, paymentIntentId, ReservationStatus.INIT, LocalDateTime.now(), LocalDateTime.now());
        log.info("Saving payment in init status.");
        paymentRepository.save(payment);
        log.info("Payment in init status has been saved.");
    }

    private String extractMail(ReservationRequest reservationRequest) {
        Iterator<PassengerWithDiscount> iterator = reservationRequest
                .getIdPassengersWithDiscounts()
                .iterator();
        if (iterator.hasNext()) {
            PassengerWithDiscount passengerWithDiscount = iterator.next();
            Long passengerId = passengerWithDiscount.getPassengerId();
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
        } else if (CREATED.equals(event.getType())) {
            log.info("PaymentIntent is created! id: {} , status: {}", id, status);
            updateStatusAndModificationDate(payment, IN_PROGRESS);
        } else {
            log.warn("Unhandled event type: {} ", event.getType());
        }
    }

    private void updateStatusAndModificationDate(Payment payment, ReservationStatus status) {
        payment.setStatus(status);
        payment.setUpdatedAt(LocalDateTime.now());
        Long reservationId = payment.getReservationId();
        Reservation reservation = reservationService.getEntityById(reservationId);
        reservation.setStatus(status);

        log.info("Updating payment to status {}", status.name());
        paymentRepository.save(payment);
        log.info("Payment status has been successfully updated");
        reservationService.updateStatus(reservation, status);
    }

    public Payment getPayment(String paymentContentId) {
        Payment payment = paymentRepository.findByPaymentId(paymentContentId);
        if (payment == null) {
            log.error("Cannot find {} with id = {}", Payment.class, paymentContentId);
            throw new EntityNotFoundException(Payment.class, "id = " + paymentContentId);
        }
        return payment;
    }
}
