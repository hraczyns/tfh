package com.hraczynski.trains.payment;

import com.hraczynski.trains.passengers.Passenger;
import com.hraczynski.trains.passengers.PassengerService;
import com.hraczynski.trains.passengers.PassengerWithDiscount;
import com.hraczynski.trains.payment.client.dto.CreatePaymentResponse;
import com.hraczynski.trains.payment.client.dto.SimplePassengerForPaymentSummaryDto;
import com.hraczynski.trains.reservations.ReservationRequest;
import com.hraczynski.trains.stoptime.StopTimeDTO;
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

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private static final String SUCCEEDED = "payment_intent.succeeded";

    @Value("${stripe-secret-key}")
    private String secret;
    @Value("${stripe-webhook-secret}")
    private String stripeWebhookSecret;
    private final PriceService priceService;
    private final StopTimeMapper stopTimeMapper;
    private final PassengerService passengerService;

    @PostConstruct
    void initKey() {
        Stripe.apiKey = secret;
    }

    public CreatePaymentResponse createPaymentIntent(ReservationRequest reservationRequest) throws StripeException {

        BigDecimal sumFromReservation = priceService.getSumFromReservation(reservationRequest);
        List<String> paymentMethodTypes = new ArrayList<>();
        paymentMethodTypes.add("p24");
        paymentMethodTypes.add("card");
        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setAmount((long) (sumFromReservation.doubleValue() * 100L))
                        .setCurrency("pln")
                        .setReceiptEmail(extractMail(reservationRequest))
                        .addAllPaymentMethodType(paymentMethodTypes)
                        .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);

        List<StopTimeDTO> route = getRoute(reservationRequest.getReservedRoute());
        List<SimplePassengerForPaymentSummaryDto> passengers = getPassengersInfo(reservationRequest);
        String email = params.getReceiptEmail();

        return new CreatePaymentResponse(paymentIntent.getClientSecret(), sumFromReservation, route, passengers, email);
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
        return null;
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
                .map(p -> new SimplePassengerForPaymentSummaryDto(p.getName() + " " + p.getSurname(),getDiscountName(p.getDiscountCode())))
                .collect(Collectors.toList()));

        return passengers;
    }

    private String getDiscountName(String discountCode){
        Discount discount = Discount.findByCode(discountCode);
        return discount != null ? discount.name() : "";
    }

    private List<StopTimeDTO> getRoute(List<Long> reservedRoute) {
        List<StopTimeDTO> stopTimes = stopTimeMapper.idsToDTOs(reservedRoute);
        List<StopTimeDTO> route = new ArrayList<>();
        route.add(stopTimes.get(0));
        route.add(stopTimes.get(stopTimes.size() - 1));
        return route;
    }

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

        if (SUCCEEDED.equals(event.getType())) {
            PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
            String id = paymentIntent.getId();
            String status = paymentIntent.getStatus();
            log.info("PaymentIntent was successful! id: {} , status: {}", id, status);
        } else {
            log.warn("Unhandled event type: {} ", event.getType());
        }
    }
}
