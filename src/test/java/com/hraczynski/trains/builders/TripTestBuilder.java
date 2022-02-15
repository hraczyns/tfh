package com.hraczynski.trains.builders;

import com.hraczynski.trains.stoptime.StopTime;
import com.hraczynski.trains.stoptime.StopTimeDto;
import com.hraczynski.trains.train.Train;
import com.hraczynski.trains.trip.Trip;
import com.hraczynski.trains.trip.TripDto;
import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TripTestBuilder {
    private static final Long ID = 1L;
    private static final BigDecimal PRICE = BigDecimal.valueOf(50.00);
    private static final double DISTANCE = 100.5;
    private static final Train TRAIN = new Train().setId(21L);
    private static final List<StopTimeDto> STOP_TIMES_DtoS = new ArrayList<>(Arrays.asList(
            new StopTimeDto(), new StopTimeDto(), new StopTimeDto()
    ));
    private static final List<StopTime> STOP_TIMES = new ArrayList<>(Arrays.asList(
            new StopTime(), new StopTime(), new StopTime()
    ));

    public static final Property<TripDto, Long> idDto = Property.newProperty();
    public static final Property<TripDto, List<StopTimeDto>> stopTimesDtos = Property.newProperty();

    public static final Property<Trip, Long> idEntity = Property.newProperty();
    public static final Property<Trip, BigDecimal> priceEntity = Property.newProperty();
    public static final Property<Trip, Double> distanceEntity = Property.newProperty();
    public static final Property<Trip, Train> trainEntity = Property.newProperty();
    public static final Property<Trip, List<StopTime>> stopTimesEntities = Property.newProperty();

    public static final Instantiator<TripDto> BasicTripDto = propertyLookup -> {
        TripDto tripDto = new TripDto();
        tripDto.setId(propertyLookup.valueOf(idDto, ID));
        tripDto.setStopTimeDtoList(propertyLookup.valueOf(stopTimesDtos, STOP_TIMES_DtoS));
        return tripDto;
    };

    public static final Instantiator<Trip> BasicTripEntity = propertyLookup -> {
        Trip trip = new Trip();
        trip.setId(propertyLookup.valueOf(idEntity, ID));
        trip.setDistance(propertyLookup.valueOf(distanceEntity, DISTANCE));
        trip.setPrice(propertyLookup.valueOf(priceEntity, PRICE));
        trip.setTrain(propertyLookup.valueOf(trainEntity, TRAIN));
        trip.setStopTimes(propertyLookup.valueOf(stopTimesEntities, STOP_TIMES));
        return trip;
    };

}
