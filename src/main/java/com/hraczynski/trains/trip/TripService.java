package com.hraczynski.trains.trip;


import org.springframework.hateoas.CollectionModel;

public interface TripService {

    TripDTO getById(Long id);
    CollectionModel<TripDTO> getAll();
    CollectionModel<TripDTO> getTripsByTrainId(Long id);
}
