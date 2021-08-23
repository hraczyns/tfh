package com.hraczynski.trains.algorithm.algorithmentities;

import com.hraczynski.trains.trip.Trip;
import lombok.Getter;
import com.hraczynski.trains.city.City;

import java.time.Duration;

@Getter
public class Results {
    private final ConnectionType type;
    private final Trip trip;
    private final int boardingPoint;
    private final int stopIndex;
    private final City origin;
    private final City destination;
    private final Duration duration;
    private final double distance;

    public Results(Trip trip, int boardingPoint, int stopIndex) {
        this.type = ConnectionType.NORMAL;
        this.trip = trip;
        this.boardingPoint = boardingPoint;
        this.stopIndex = stopIndex;
        this.origin = null;
        this.destination = null;
        this.duration = null;
        this.distance = 0;
    }

    public Results(City origin, City destination, Duration duration, double distance) {
        this.type = ConnectionType.TRANSFER;
        this.origin = origin;
        this.destination = destination;
        this.duration = duration;
        this.distance = distance;
        this.trip = null;
        this.boardingPoint = 0;
        this.stopIndex = 0;

    }
}
