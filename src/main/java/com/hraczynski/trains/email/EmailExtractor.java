package com.hraczynski.trains.email;

import com.hraczynski.trains.passengers.unregistered.PassengerNotRegistered;
import com.hraczynski.trains.passengers.unregistered.PassengerNotRegisteredMapper;
import com.hraczynski.trains.reservations.Reservation;
import com.hraczynski.trains.reservations.ReservationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailExtractor {

    private final PassengerNotRegisteredMapper passengerNotRegisteredMapper;

    public List<String> getEmails(Reservation reservation, ReservationRequest request) {
        log.info("Extracting emails from reservation");
        List<String> list = reservation.getPassengers()
                .stream()
                .map(p -> p.getPassenger().getEmail())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        list.addAll(request.getPassengerNotRegisteredList()
                .stream()
                .map(PassengerNotRegistered::getEmail)
                .filter(Objects::nonNull)
                .collect(Collectors.toList())
        );
        return list;
    }

    public List<String> getEmails(Reservation reservation) {
        log.info("Extracting emails from reservation");
        List<String> list = reservation.getPassengers()
                .stream()
                .map(p -> p.getPassenger().getEmail())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        List<PassengerNotRegistered> passengersNotRegistered = passengerNotRegisteredMapper.deserialize(reservation.getPassengersNotRegistered());
        list.addAll(passengersNotRegistered.stream()
                .map(PassengerNotRegistered::getEmail)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        return list;
    }
}
