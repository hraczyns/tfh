package com.hraczynski.trains.builders;

import com.hraczynski.trains.passengers.Passenger;
import com.hraczynski.trains.passengers.PassengerDTO;
import com.hraczynski.trains.passengers.PassengerRequest;
import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;

import java.time.LocalDate;

public abstract class PassengerTestBuilder {

    private static final String NAME = "Hubert";
    private static final String SURNAME = "Hubertowski";
    private static final String GENDER = "m";
    private static final LocalDate BORN_DATE = LocalDate.now();
    private static final String COUNTRY_STRING = "Poland";
    private static final Long ID = 11L;

    public static final Property<PassengerDTO, String> nameDTO = Property.newProperty();
    public static final Property<PassengerDTO, String> surnameDTO = Property.newProperty();
    public static final Property<PassengerDTO, String> countryDTO = Property.newProperty();
    public static final Property<PassengerDTO, Long> idDTO = Property.newProperty();
    public static final Property<PassengerDTO, String> genderDTO = Property.newProperty();
    public static final Property<PassengerDTO, LocalDate> bornDateDTO = Property.newProperty();

    public static final Property<PassengerRequest, String> nameRequest = Property.newProperty();
    public static final Property<PassengerRequest, String> countryRequest = Property.newProperty();
    public static final Property<PassengerRequest, Long> idRequest = Property.newProperty();
    public static final Property<PassengerRequest, String> surnameRequest = Property.newProperty();
    public static final Property<PassengerRequest, String> genderRequest = Property.newProperty();
    public static final Property<PassengerRequest, LocalDate> bornDateRequest = Property.newProperty();

    public static final Property<Passenger, String> nameEntity = Property.newProperty();
    public static final Property<Passenger, String> countryEntity = Property.newProperty();
    public static final Property<Passenger, Long> idEntity = Property.newProperty();
    public static final Property<Passenger, String> surnameEntity = Property.newProperty();
    public static final Property<Passenger, String> genderEntity = Property.newProperty();
    public static final Property<Passenger, LocalDate> bornDateEntity = Property.newProperty();

    public static final Instantiator<PassengerDTO> BasicPassengerDTO = propertyLookup -> {
        PassengerDTO passengerDTO = new PassengerDTO();
        passengerDTO.setCountry(propertyLookup.valueOf(countryDTO, COUNTRY_STRING));
        passengerDTO.setName(propertyLookup.valueOf(nameDTO, NAME));
        passengerDTO.setId(propertyLookup.valueOf(idDTO, ID));
        passengerDTO.setSurname(propertyLookup.valueOf(surnameDTO, SURNAME));
        passengerDTO.setGender(propertyLookup.valueOf(genderDTO, GENDER));
        passengerDTO.setBornDate(propertyLookup.valueOf(bornDateDTO, BORN_DATE));
        return passengerDTO;
    };

    public static final Instantiator<PassengerRequest> BasicPassengerRequest = propertyLookup -> {
        PassengerRequest passengerRequest = new PassengerRequest();
        passengerRequest.setCountry(propertyLookup.valueOf(countryRequest, COUNTRY_STRING));
        passengerRequest.setName(propertyLookup.valueOf(nameRequest, NAME));
        passengerRequest.setId(propertyLookup.valueOf(idRequest, ID));
        passengerRequest.setSurname(propertyLookup.valueOf(surnameRequest, SURNAME));
        passengerRequest.setGender(propertyLookup.valueOf(genderRequest, GENDER));
        passengerRequest.setBornDate(propertyLookup.valueOf(bornDateRequest, BORN_DATE));
        return passengerRequest;
    };

    public static final Instantiator<Passenger> BasicPassengerEntity = propertyLookup -> {
        Passenger passenger = new Passenger();
        passenger.setCountry(propertyLookup.valueOf(countryEntity, COUNTRY_STRING));
        passenger.setName(propertyLookup.valueOf(nameEntity, NAME));
        passenger.setId(propertyLookup.valueOf(idEntity, ID));
        passenger.setSurname(propertyLookup.valueOf(surnameEntity, SURNAME));
        passenger.setGender(propertyLookup.valueOf(genderEntity, GENDER));
        passenger.setBornDate(propertyLookup.valueOf(bornDateEntity, BORN_DATE));

        //TODO reservations
        return passenger;
    };

}

