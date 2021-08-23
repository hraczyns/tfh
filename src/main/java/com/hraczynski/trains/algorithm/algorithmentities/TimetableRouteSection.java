package com.hraczynski.trains.algorithm.algorithmentities;

import com.hraczynski.trains.city.City;
import com.hraczynski.trains.trip.Trip;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = false)
public class TimetableRouteSection extends RouteSection {
    private final Trip trip;

    public TimetableRouteSection(City source, City destination, Trip trip) {
        super(source, destination);
        this.trip = trip;
    }
}
