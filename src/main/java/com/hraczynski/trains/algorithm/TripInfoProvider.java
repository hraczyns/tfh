package com.hraczynski.trains.algorithm;

import com.hraczynski.trains.algorithm.algorithmentities.RouteStops;
import com.hraczynski.trains.city.City;
import com.hraczynski.trains.stoptime.StopTime;
import com.hraczynski.trains.trip.Trip;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public record TripInfoProvider(Set<Trip> trips) {
    private static final int LIMIT_YEAR = 5000;

    public Optional<Trip> getEarliestTripAtStop(RouteStops route, int index, LocalDateTime time) {
        return trips.stream()
                .filter(filterTripWithRoute(route))
                .filter(s -> {
                    LocalDateTime departureTime = s.getStopTimes().get(index).getDepartureTime();
                    return departureTime.getYear() < LIMIT_YEAR && departureTime.isAfter(time);
                })
                .findFirst();
    }

    private Predicate<? super Trip> filterTripWithRoute(RouteStops route) {
        return s -> {
            List<City> stopList = route.stopList();
            List<City> stopListFromS = s.getStopTimes().stream().map(StopTime::getStop).collect(Collectors.toList());
            return stopList.equals(stopListFromS);
        };
    }
}
