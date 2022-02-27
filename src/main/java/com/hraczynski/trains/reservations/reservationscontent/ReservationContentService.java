package com.hraczynski.trains.reservations.reservationscontent;

import com.hraczynski.trains.exceptions.definitions.EntityNotFoundException;
import com.hraczynski.trains.passengers.PassengerNotRegisteredMapper;
import com.hraczynski.trains.payment.Payment;
import com.hraczynski.trains.payment.PaymentService;
import com.hraczynski.trains.reservations.Reservation;
import com.hraczynski.trains.reservations.ReservationRepository;
import com.hraczynski.trains.reservations.ReservationStatus;
import com.hraczynski.trains.stoptime.StopTime;
import com.hraczynski.trains.ticket.TicketService;
import com.hraczynski.trains.train.Train;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationContentService {

    private final PaymentService paymentService;
    private final ReservationRepository reservationRepository;
    private final TicketService ticketService;
    private final PassengerNotRegisteredMapper passengerNotRegisteredMapper;

    @SuppressWarnings("all")
    public ReservationContentDto getContent(String paymentContentId) {
        Payment payment = paymentService.getPayment(paymentContentId);
        ReservationStatus status = payment.getStatus();
        if (status != ReservationStatus.COMPLETED) {
            return new ReservationContentDto(null, new byte[]{});
        }
        Reservation reservation = retrieveReservation(payment.getReservationId());
        Map<String, Object> map = new HashMap<>();
        List<StopTime> reservedRoute = reservation.getReservedRoute();
        map.put("from", reservedRoute.get(0).getStop().getName());
        map.put("to", reservedRoute.get(reservedRoute.size() - 1).getStop().getName());
        map.put("date", reservedRoute.get(0).getDepartureTime());
        map.put("trainId", reservation.getTrains().stream().map(Train::getRepresentationUnique).collect(Collectors.joining(", ")));
        map.put("identifier", reservation.getIdentifier());
        map.put("passengers", passengerNotRegisteredMapper.deserialize(reservation.getPassengersNotRegistered()));
        File file = ticketService.prepareTicketPdf(map, reservation.getIdentifier());
        try {
            if (file != null && file.exists()) {
                return new ReservationContentDto(file.getPath(), Files.readAllBytes(Paths.get(file.getAbsolutePath())));
            }
            return new ReservationContentDto(null, new byte[]{});
        } catch (IOException e) {
            return new ReservationContentDto(null, new byte[]{});
        } finally {
            if (file != null) {
                file.delete();
            }
        }

    }

    private Reservation retrieveReservation(Long reservationId) {
        return reservationRepository.findById(reservationId).orElseThrow(() -> {
            log.error("Cannot find {} with id = {}", Reservation.class, reservationId);
            return new EntityNotFoundException(Reservation.class, "id = " + reservationId);
        });
    }
}
