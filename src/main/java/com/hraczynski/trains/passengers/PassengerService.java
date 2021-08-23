package com.hraczynski.trains.passengers;

import org.springframework.hateoas.CollectionModel;

public interface PassengerService {
    CollectionModel<PassengerDTO> getAll();

    PassengerDTO getById(Long id);

    PassengerDTO addPassenger(PassengerRequest request);

    PassengerDTO deleteById(Long id);

    PassengerDTO updateById(PassengerRequest request);

    PassengerDTO patchById(PassengerRequest request);
}
