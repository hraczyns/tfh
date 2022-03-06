package com.hraczynski.trains.payment;

import com.hraczynski.trains.payment.client.dto.CreatePaymentResponse;
import com.hraczynski.trains.reservations.ReservationRequest;
import com.hraczynski.trains.reservations.ReservationShortResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class PaymentController {

    private final PriceService priceService;
    private final PaymentService paymentService;

    @GetMapping("/estimation")
    public ResponseEntity<PriceResponse> getPriceResponse(@RequestParam(name = "ids") String stopTimeIds) {
        return new ResponseEntity<>(priceService.getPrice(stopTimeIds), HttpStatus.OK);
    }

    @GetMapping("/calculation_with_discount")
    public ResponseEntity<PriceWithDiscount> getPriceWithDiscount(@RequestParam(name = "price") String price, @RequestParam(name = "discount") String discount) {
        return new ResponseEntity<>(priceService.getPriceWithDiscount(price, discount), HttpStatus.OK);
    }

    @PostMapping("/create-payment-intent")
    public ResponseEntity<CreatePaymentResponse> createPaymentIntent(@Valid @RequestBody ReservationRequest reservationRequest) {
        CreatePaymentResponse paymentIntent = paymentService.createPaymentIntent(reservationRequest);
        return new ResponseEntity<>(paymentIntent, HttpStatus.OK);
    }

    @PostMapping("/stripe/events")
    public void handleEvents(@RequestHeader("Stripe-Signature") String sigStripe, @RequestBody String payload) {
        paymentService.handleEvents(sigStripe, payload);
    }

    @GetMapping("/shortResponse/reservations")
    public ResponseEntity<ReservationShortResponse> getReservationIdentifierByPaymentId(@RequestParam("paymentId") String paymentId){
        return new ResponseEntity<>(paymentService.getReservationIdentifierByPaymentId(paymentId), HttpStatus.OK);
    }

}
