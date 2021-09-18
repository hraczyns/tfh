package com.hraczynski.trains.controllers;

import com.hraczynski.trains.city.CityDTO;
import com.hraczynski.trains.city.CityRequest;
import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;

public abstract class CityTestBuilder {

    private static final String NAME = "Radom";
    private static final String COUNTRY_STRING = "Poland";
    private static final Long ID = 11L;
    private static final double LON = 20.0;
    private static final double LAT = 31.0;

    public static final Property<CityDTO, String> nameDTO = Property.newProperty();
    public static final Property<CityDTO, String> countryDTO = Property.newProperty();
    public static final Property<CityDTO, Long> idDTO = Property.newProperty();
    public static final Property<CityDTO, Double> lonDTO = Property.newProperty();
    public static final Property<CityDTO, Double> latDTO = Property.newProperty();

    public static final Property<CityRequest, String> nameRequest = Property.newProperty();
    public static final Property<CityRequest, String> countryRequest = Property.newProperty();
    public static final Property<CityRequest, Long> idRequest = Property.newProperty();
    public static final Property<CityRequest, Double> lonRequest = Property.newProperty();
    public static final Property<CityRequest, Double> latRequest = Property.newProperty();

    public static final Instantiator<CityDTO> BasicCityDTO = propertyLookup -> {
        CityDTO cityDTO = new CityDTO();
        cityDTO.setCountry(propertyLookup.valueOf(countryDTO, COUNTRY_STRING));
        cityDTO.setName(propertyLookup.valueOf(nameDTO, NAME));
        cityDTO.setId(propertyLookup.valueOf(idDTO, ID));
        cityDTO.setLat(propertyLookup.valueOf(latDTO, LAT));
        cityDTO.setLon(propertyLookup.valueOf(lonDTO, LON));
        return cityDTO;
    };

    public static final Instantiator<CityRequest> BasicCityRequest = propertyLookup -> {
        CityRequest cityRequest = new CityRequest();
        cityRequest.setCountry(propertyLookup.valueOf(countryRequest, COUNTRY_STRING));
        cityRequest.setName(propertyLookup.valueOf(nameRequest, NAME));
        cityRequest.setId(propertyLookup.valueOf(idRequest, ID));
        cityRequest.setLat(propertyLookup.valueOf(latRequest, LAT));
        cityRequest.setLon(propertyLookup.valueOf(lonRequest, LON));
        return cityRequest;
    };



}
