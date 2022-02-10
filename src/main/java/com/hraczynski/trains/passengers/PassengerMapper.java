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

    public Passenger requestToEntityByRepo(PassengerRequest request) {
        Passenger foundEnt = repository.findById(request.getId())
                .orElseThrow(() -> {
                    log.error("Cannot find Passenger with id = {}", request.getId());
                    return new EntityNotFoundException(Passenger.class, "id = " + request.getId());
                });
        return new Passenger(request.getId(), request.getName(), request.getSurname(), request.getCountry(), request.getBornDate(), request.getEmail(), foundEnt.getReservations());
    }

    public Passenger requestToEntity(PassengerRequest request, Set<Reservation> reservations) {
        return new Passenger(request.getId(), request.getName(), request.getSurname(), request.getCountry(), request.getBornDate(), request.getEmail(), reservations);
    }

    public PassengerDTO idToDTO(Long id) {
        Passenger passenger = repository.findById(id).orElseThrow(() -> {
            log.error("Cannot find Passenger with id = {}", id);
            return new EntityNotFoundException(Passenger.class, "id = " + id);
        });
        return new PassengerDTO()
                .setId(id)
                .setCountry(passenger.getCountry())
                .setName(passenger.getName())
                .setSurname(passenger.getSurname())
                .setBornDate(passenger.getBornDate())
                .setEmail(passenger.getEmail());
    }
}
