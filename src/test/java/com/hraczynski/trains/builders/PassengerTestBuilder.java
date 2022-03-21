package com.hraczynski.trains.builders;

import com.hraczynski.trains.passengers.Passenger;
import com.hraczynski.trains.passengers.PassengerDto;
import com.hraczynski.trains.passengers.PassengerRequest;
import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;

public abstract class PassengerTestBuilder {

    private static final String NAME = "Hubert";
    private static final String SURNAME = "Hubertowski";
    private static final String EMAIL = "email@email.com";
    private static final Long ID = 11L;

    public static final Property<PassengerDto, String> nameDto = Property.newProperty();
    public static final Property<PassengerDto, String> surnameDto = Property.newProperty();
    public static final Property<PassengerDto, Long> idDto = Property.newProperty();
    public static final Property<PassengerDto, String> emailDto = Property.newProperty();

    public static final Property<PassengerRequest, String> nameRequest = Property.newProperty();
    public static final Property<PassengerRequest, Long> idRequest = Property.newProperty();
    public static final Property<PassengerRequest, String> surnameRequest = Property.newProperty();
    public static final Property<PassengerRequest, String> emailRequest = Property.newProperty();

    public static final Property<Passenger, String> nameEntity = Property.newProperty();
    public static final Property<Passenger, Long> idEntity = Property.newProperty();
    public static final Property<Passenger, String> surnameEntity = Property.newProperty();
    public static final Property<Passenger, String> emailEntity = Property.newProperty();

    public static final Instantiator<PassengerDto> BasicPassengerDto = propertyLookup -> {
        PassengerDto passengerDto = new PassengerDto();
        passengerDto.setName(propertyLookup.valueOf(nameDto, NAME));
        passengerDto.setId(propertyLookup.valueOf(idDto, ID));
        passengerDto.setSurname(propertyLookup.valueOf(surnameDto, SURNAME));
        passengerDto.setEmail(propertyLookup.valueOf(emailDto, EMAIL));
        return passengerDto;
    };

    public static final Instantiator<PassengerRequest> BasicPassengerRequest = propertyLookup -> {
        PassengerRequest passengerRequest = new PassengerRequest();
        passengerRequest.setName(propertyLookup.valueOf(nameRequest, NAME));
        passengerRequest.setId(propertyLookup.valueOf(idRequest, ID));
        passengerRequest.setSurname(propertyLookup.valueOf(surnameRequest, SURNAME));
        passengerRequest.setEmail(propertyLookup.valueOf(emailRequest, EMAIL));

        return passengerRequest;
    };

    public static final Instantiator<Passenger> BasicPassengerEntity = propertyLookup -> {
        Passenger passenger = new Passenger();
        passenger.setName(propertyLookup.valueOf(nameEntity, NAME));
        passenger.setId(propertyLookup.valueOf(idEntity, ID));
        passenger.setSurname(propertyLookup.valueOf(surnameEntity, SURNAME));
        passenger.setEmail(propertyLookup.valueOf(emailEntity, EMAIL));

        //TODO reservations
        return passenger;
    };

}

