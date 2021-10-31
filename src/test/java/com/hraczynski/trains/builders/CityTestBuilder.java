package com.hraczynski.trains.builders;

import com.hraczynski.trains.city.City;
import com.hraczynski.trains.city.CityDTO;
import com.hraczynski.trains.city.CityRequest;
import com.hraczynski.trains.country.Country;
import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Maker;
import com.natpryce.makeiteasy.Property;

import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.with;

public abstract class CityTestBuilder {

    private static final String NAME = "Radom";
    private static final String COUNTRY_STRING = "Poland";
    private static final Long ID = 11L;
    private static final double LON = 20.0;
    private static final double LAT = 31.0;
    private static final Country COUNTRY_OBJECT = new Country(1L, COUNTRY_STRING);

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

    public static final Property<City, String> nameEntity = Property.newProperty();
    public static final Property<City, Country> countryEntity = Property.newProperty();
    public static final Property<City, Long> idEntity = Property.newProperty();
    public static final Property<City, Double> lonEntity = Property.newProperty();
    public static final Property<City, Double> latEntity = Property.newProperty();


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

    public static final Instantiator<City> BasicCityEntity = propertyLookup -> {
        City city = new City();
        city.setId(propertyLookup.valueOf(idEntity, ID));
        city.setCountry(propertyLookup.valueOf(countryEntity, COUNTRY_OBJECT));
        city.setName(propertyLookup.valueOf(nameEntity, NAME));
        city.setLat(propertyLookup.valueOf(latEntity, LAT));
        city.setLon(propertyLookup.valueOf(lonEntity, LON));
        return city;
    };

    public static final Instantiator<CityRequest> EmptyCityRequestWithOnlyId = propertyLookup -> new CityRequest().setId(propertyLookup.valueOf(idRequest, 2L));

    public static final Maker<City> PatchCityEntityMaker = a(BasicCityEntity)
            .but(with(nameEntity, "randomText"))
            .but(with(latEntity, Double.MIN_VALUE))
            .but(with(lonEntity, Double.MAX_VALUE));
}
