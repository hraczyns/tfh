package com.hraczynski.trains.reservations.reservationscontent;

import com.hraczynski.trains.exceptions.definitions.EntityNotFoundException;
import com.hraczynski.trains.passengers.PassengerNotRegistered;
import com.hraczynski.trains.passengers.PassengerNotRegisteredMapper;
import com.hraczynski.trains.payment.Discount;
import com.hraczynski.trains.payment.Payment;
import com.hraczynski.trains.payment.Price;
import com.hraczynski.trains.reservations.Reservation;
import com.hraczynski.trains.reservations.ReservationRepository;
import com.hraczynski.trains.reservations.ReservationStatus;
import com.hraczynski.trains.stoptime.StopTime;
import com.hraczynski.trains.ticket.TicketService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationContentService {

    private final ReservationRepository reservationRepository;
    private final TicketService ticketService;
    private final PassengerNotRegisteredMapper passengerNotRegisteredMapper;

    @SuppressWarnings("all")
    public ReservationContentDto getContent(Payment payment) {
        if (payment == null) {
            log.error("Payment does not exist. Cannot create ticket!");
            return new ReservationContentDto(null, new byte[]{});
        }
        ReservationStatus status = payment.getStatus();
        if (status != ReservationStatus.COMPLETED) {
            log.warn("Payment {} is in {} status already", payment.getPaymentId(), status);
            return new ReservationContentDto(null, new byte[]{});
        }
        Reservation reservation = retrieveReservation(payment.getReservationId());
        log.info("Retrieved reservation");
        File file = getTicket(reservation);
        try {
            if (file != null && file.exists()) {
                log.info("Successfully created pdf ticket for reservation binded with payment {}", payment.getPaymentId());
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

    public File getTicket(Reservation reservation) {
        Map<String, Object> forTicketParams = getForTicketParams(reservation);
        return ticketService.prepareTicketPdf(forTicketParams, reservation.getIdentifier());
    }

    private Map<String, Object> getForTicketParams(Reservation reservation) {
        log.info("Preparing params for ticket creation");
        Map<String, Object> map = new HashMap<>();
        List<StopTime> reservedRoute = reservation.getReservedRoute();
        StopTime firstStop = reservedRoute.get(0);
        StopTime lastStop = reservedRoute.get(reservedRoute.size() - 1);
        map.put("from", firstStop.getStop().getName());
        map.put("to", lastStop.getStop().getName());
        map.put("date", formatDate(firstStop.getDepartureTime()) + " - " + formatDate(lastStop.getArrivalTime()));
        map.put("passengers", preparePassengers(reservation.getPrices(), passengerNotRegisteredMapper.deserialize(reservation.getPassengersNotRegistered())));
        map.put("routeDetails", prepareRouteDetails(reservedRoute));
        return map;
    }

    // to consider when passengers with id come
    private List<PassengerDetails> preparePassengers(Set<Price> prices, List<PassengerNotRegistered> passengers) {
        log.info("Preparing passengers for ticket");
        List<PassengerDetails> passengerDetails = new ArrayList<>();
        if (prices == null || prices.isEmpty() || passengers == null || passengers.isEmpty()) {
            log.error("Passengers or prices are corrupted in already processing ticket creation request.");
            return Collections.emptyList();
        }
        for (Price price : prices) {
            PassengerNotRegistered passenger = passengers.stream()
                    .filter(p -> p.getName().equals(price.getName())
                            && p.getSurname().equals(price.getSurname())
                            && discountEquals(p.getDiscountCode(), price.getDiscount()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("None price matches passengers"));
            String discount;
            if (price.getDiscount() != null) {
                discount = StringUtils.capitalize(price.getDiscount().name().toLowerCase());
            } else {
                discount = "-";
            }
            passengerDetails.add(new PassengerDetails(
                    passenger.getName(),
                    passenger.getSurname(),
                    discount,
                    price.getPrice().setScale(2, RoundingMode.HALF_UP).toString()
            ));
        }
        log.info("Successfully prepared passengers");
        return passengerDetails;
    }

    private boolean discountEquals(String discountCode, Discount discount) {
        return (discountCode != null && discount != null && discountCode.equals(discount.getCode())) || (discountCode == null && discount == null);
    }

    private List<RouteDetails> prepareRouteDetails(List<StopTime> reservedRoute) {
        log.info("Preparing route details");
        List<RouteDetails> routeDetails = new ArrayList<>();
        for (int i = 1; i < reservedRoute.size(); i += 2) {
            StopTime source = reservedRoute.get(i - 1);
            StopTime destination = reservedRoute.get(i);

            routeDetails.add(new RouteDetails(
                    source.getStop().getName(),
                    destination.getStop().getName(),
                    formatDate(source.getDepartureTime()),
                    formatDate(destination.getArrivalTime()),
                    source.getTrip().getTrain().getRepresentationUnique(),
                    source.getTrip().getTrain().getModel().name()
            ));
        }
        log.info("Successfully prepared route details");
        return routeDetails;
    }

    private String formatDate(LocalDateTime date) {
        if (LocalDateTime.MAX.isEqual(date) || LocalDateTime.MIN.isEqual(date)) {
            return "-";
        }
        return date.toString().replace("T", " ");
    }

    private Reservation retrieveReservation(Long reservationId) {
        return reservationRepository.findById(reservationId).orElseThrow(() -> {
            log.error("Cannot find {} with id = {}", Reservation.class, reservationId);
            return new EntityNotFoundException(Reservation.class, "id = " + reservationId);
        });
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class RouteDetails {
        private String source;
        private String destination;
        private String arrivalTime;
        private String departureTime;
        private String train;
        private String trainClass;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class PassengerDetails {
        private String name;
        private String surname;
        private String discount;
        private String price;
    }
}

