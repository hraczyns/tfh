package com.hraczynski.trains.builders;

import com.hraczynski.trains.passengers.Passenger;
import com.hraczynski.trains.passengers.PassengerDto;
import com.hraczynski.trains.passengers.PassengerRequest;
import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;

import java.time.LocalDate;

public abstract class PassengerTestBuilder {

    private static final String NAME = "Hubert";
    private static final String SURNAME = "Hubertowski";
    private static final String EMAIL = "email@email.com";
    private static final LocalDate BORN_DATE = LocalDate.now();
    private static final String COUNTRY_STRING = "Poland";
    private static final Long ID = 11L;

    public static final Property<PassengerDto, String> nameDto = Property.newProperty();
    public static final Property<PassengerDto, String> surnameDto = Property.newProperty();
    public static final Property<PassengerDto, String> countryDto = Property.newProperty();
    public static final Property<PassengerDto, Long> idDto = Property.newProperty();
    public static final Property<PassengerDto, LocalDate> bornDateDto = Property.newProperty();
    public static final Property<PassengerDto, String> emailDto = Property.newProperty();

    public static final Property<PassengerRequest, String> nameRequest = Property.newProperty();
    public static final Property<PassengerRequest, String> countryRequest = Property.newProperty();
    public static final Property<PassengerRequest, Long> idRequest = Property.newProperty();
    public static final Property<PassengerRequest, String> surnameRequest = Property.newProperty();
    public static final Property<PassengerRequest, LocalDate> bornDateRequest = Property.newProperty();
    public static final Property<PassengerRequest, String> emailRequest = Property.newProperty();

    public static final Property<Passenger, String> nameEntity = Property.newProperty();
    public static final Property<Passenger, String> countryEntity = Property.newProperty();
    public static final Property<Passenger, Long> idEntity = Property.newProperty();
    public static final Property<Passenger, String> surnameEntity = Property.newProperty();
    public static final Property<Passenger, LocalDate> bornDateEntity = Property.newProperty();
    public static final Property<Passenger, String> emailEntity = Property.newProperty();

    public static final Instantiator<PassengerDto> BasicPassengerDto = propertyLookup -> {
        PassengerDto passengerDto = new PassengerDto();
        passengerDto.setCountry(propertyLookup.valueOf(countryDto, COUNTRY_STRING));
        passengerDto.setName(propertyLookup.valueOf(nameDto, NAME));
        passengerDto.setId(propertyLookup.valueOf(idDto, ID));
        passengerDto.setSurname(propertyLookup.valueOf(surnameDto, SURNAME));
        passengerDto.setBornDate(propertyLookup.valueOf(bornDateDto, BORN_DATE));
        passengerDto.setEmail(propertyLookup.valueOf(emailDto, EMAIL));
        return passengerDto;
    };

    public static final Instantiator<PassengerRequest> BasicPassengerRequest = propertyLookup -> {
        PassengerRequest passengerRequest = new PassengerRequest();
        passengerRequest.setCountry(propertyLookup.valueOf(countryRequest, COUNTRY_STRING));
        passengerRequest.setName(propertyLookup.valueOf(nameRequest, NAME));
        passengerRequest.setId(propertyLookup.valueOf(idRequest, ID));
        passengerRequest.setSurname(propertyLookup.valueOf(surnameRequest, SURNAME));
        passengerRequest.setBornDate(propertyLookup.valueOf(bornDateRequest, BORN_DATE));
        passengerRequest.setEmail(propertyLookup.valueOf(emailRequest, EMAIL));

        return passengerRequest;
    };

    public static final Instantiator<Passenger> BasicPassengerEntity = propertyLookup -> {
        Passenger passenger = new Passenger();
        passenger.setCountry(propertyLookup.valueOf(countryEntity, COUNTRY_STRING));
        passenger.setName(propertyLookup.valueOf(nameEntity, NAME));
        passenger.setId(propertyLookup.valueOf(idEntity, ID));
        passenger.setSurname(propertyLookup.valueOf(surnameEntity, SURNAME));
        passenger.setBornDate(propertyLookup.valueOf(bornDateEntity, BORN_DATE));
        passenger.setEmail(propertyLookup.valueOf(emailEntity, EMAIL));

        //TODO reservations
        return passenger;
    };

}

