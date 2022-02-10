package com.hraczynski.trains.trip;


import java.util.Set;

public interface TripService {

    Trip getById(Long id);

    Set<Trip> getAll();

    Set<Trip> getTripsByTrainId(Long id);
}
