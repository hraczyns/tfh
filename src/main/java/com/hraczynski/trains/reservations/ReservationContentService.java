package com.hraczynski.trains.reservations;

import com.hraczynski.trains.exceptions.definitions.EntityNotFoundException;
import com.hraczynski.trains.payment.Payment;
import com.hraczynski.trains.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationContentService {

    private final PaymentService paymentService;
    private final ReservationRepository reservationRepository;

    public Object getContent(String paymentContentId) {
        Payment payment = paymentService.getPayment(paymentContentId);
        if (payment.getStatus() != ReservationStatus.COMPLETED) {
            return null; // enhance
        }
        Reservation reservation = retrieveReservation(payment.getReservationId());
        return null;
    }

    private Reservation retrieveReservation(Long reservationId) {
        return reservationRepository.findById(reservationId).orElseThrow(() -> {
            log.error("Cannot find {} with id = {}", Reservation.class, reservationId);
            return new EntityNotFoundException(Reservation.class, "id = " + reservationId);
        });
    }
}
