package com.hraczynski.trains.passengers;

import org.springframework.hateoas.CollectionModel;

import java.util.Set;

public interface PassengerService {
    Set<Passenger> getAll();

    Passenger getById(Long id);

    Passenger addPassenger(PassengerRequest request);

    Passenger deleteById(Long id);

    void update(PassengerRequest request);

    void patch(PassengerRequest request);
}
