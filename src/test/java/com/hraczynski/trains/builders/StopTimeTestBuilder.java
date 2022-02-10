package com.hraczynski.trains.builders;

import com.hraczynski.trains.city.City;
import com.hraczynski.trains.city.CityDTO;
import com.hraczynski.trains.stoptime.StopTime;
import com.hraczynski.trains.stoptime.StopTimeDTO;
import com.hraczynski.trains.stoptime.StopTimeRequest;
import com.hraczynski.trains.trip.Trip;
import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;

import java.time.LocalDateTime;

import static com.hraczynski.trains.builders.CityTestBuilder.BasicCityDTO;
import static com.hraczynski.trains.builders.CityTestBuilder.BasicCityEntity;
import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;

public class StopTimeTestBuilder {
    private static final Long ID = 11L;
    private static final City CITY = make(a(BasicCityEntity));
    private static final Long CITY_REQUEST = 11L;
    private static final CityDTO CITY_DTO = make(a(BasicCityDTO));
    private static final Trip TRIP = new Trip();
    private static final LocalDateTime DEPARTURE_TIME = LocalDateTime.now();
    private static final LocalDateTime ARRIVAL_TIME = LocalDateTime.now().minusDays(3);

    public static final Property<StopTimeDTO, Long> idDTO = Property.newProperty();
    public static final Property<StopTimeDTO, CityDTO> cityDTO = Property.newProperty();
    public static final Property<StopTimeDTO, LocalDateTime> depDTO = Property.newProperty();
    public static final Property<StopTimeDTO, LocalDateTime> arrDTO = Property.newProperty();

    public static final Property<StopTimeRequest, Long> idRequest = Property.newProperty();
    public static final Property<StopTimeRequest, Long> cityRequest = Property.newProperty();
    public static final Property<StopTimeRequest, LocalDateTime> depRequest = Property.newProperty();
    public static final Property<StopTimeRequest, LocalDateTime> arrRequest = Property.newProperty();

    public static final Property<StopTime, Long> idEntity = Property.newProperty();
    public static final Property<StopTime, City> cityEntity = Property.newProperty();
    public static final Property<StopTime, Trip> tripEntity = Property.newProperty();
    public static final Property<StopTime, LocalDateTime> depEntity = Property.newProperty();
    public static final Property<StopTime, LocalDateTime> arrEntity = Property.newProperty();


    public static final Instantiator<StopTimeDTO> BasicStopTimeDTO = propertyLookup -> {
        StopTimeDTO stopTimeDTO = new StopTimeDTO();
        stopTimeDTO.setId(propertyLookup.valueOf(idDTO, ID));
        stopTimeDTO.setCityDTO(propertyLookup.valueOf(cityDTO, CITY_DTO));
        stopTimeDTO.setDepartureTime(propertyLookup.valueOf(depDTO, DEPARTURE_TIME));
        stopTimeDTO.setArrivalTime(propertyLookup.valueOf(arrDTO, ARRIVAL_TIME));
        return stopTimeDTO;
    };

    public static final Instantiator<StopTimeRequest> BasicStopTimeRequest = propertyLookup -> {
        StopTimeRequest stopTimeRequest = new StopTimeRequest();
        stopTimeRequest.setCityId(propertyLookup.valueOf(cityRequest, CITY_REQUEST));
        stopTimeRequest.setId(propertyLookup.valueOf(idRequest, ID));
        stopTimeRequest.setDepartureTime(propertyLookup.valueOf(depRequest, DEPARTURE_TIME));
        stopTimeRequest.setArrivalTime(propertyLookup.valueOf(arrRequest, ARRIVAL_TIME));
        return stopTimeRequest;
    };

    public static final Instantiator<StopTime> BasicStopTimeEntity = propertyLookup -> {
        StopTime stopTime = new StopTime();
        stopTime.setId(propertyLookup.valueOf(idEntity, ID));
        stopTime.setStop(propertyLookup.valueOf(cityEntity, CITY));
        stopTime.setTrip(propertyLookup.valueOf(tripEntity, TRIP));
        stopTime.setDepartureTime(propertyLookup.valueOf(depEntity, DEPARTURE_TIME));
        stopTime.setArrivalTime(propertyLookup.valueOf(arrEntity, ARRIVAL_TIME));
        return stopTime;
    };
}
