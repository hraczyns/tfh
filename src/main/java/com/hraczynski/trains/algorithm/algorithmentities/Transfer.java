package com.hraczynski.trains.algorithm.algorithmentities;

import com.hraczynski.trains.city.City;
import com.hraczynski.trains.journey.PartOfJourney;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import com.hraczynski.trains.journey.JourneyRepresentationModelAssembler;

import java.time.Duration;

@Getter
@EqualsAndHashCode(callSuper = false)
@ToString
public class Transfer extends RouteSection implements PartOfJourney {
    private final Duration duration;
    private final double distance;

    public Transfer(City origin, City destination, Duration duration, double distance) {
        super(origin, destination);
        this.duration = duration;
        this.distance = distance;
    }
}
