package com.hraczynski.trains.passengers;

import com.hraczynski.trains.exceptions.definitions.EntityNotFoundException;
import com.hraczynski.trains.reservations.Reservation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class PassengerMapper {

    private final PassengerRepository repository;

    //todo enhance
    public Passenger requestToEntityByRepo(PassengerRequest request) {
        Passenger foundEnt = repository.findById(request.getId())
                .orElseThrow(() -> {
                    log.error("Cannot find Passenger with id = {}", request.getId());
                    return new EntityNotFoundException(Passenger.class, "id = " + request.getId());
                });
        Passenger passenger = new Passenger(request.getId(), request.getName(), request.getSurname(), request.getGender(), request.getCountry(), request.getNotes(), request.getBornDate(), foundEnt.getReservations());
        return passenger;
    }

    public Passenger requestToEntity(PassengerRequest request, Set<Reservation> reservations) {
        Passenger passenger = new Passenger(request.getId(), request.getName(), request.getSurname(), request.getGender(), request.getCountry(), request.getNotes(), request.getBornDate(), reservations);
        return passenger;
    }
}
