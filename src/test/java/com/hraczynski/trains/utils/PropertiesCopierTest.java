package com.hraczynski.trains.utils;

import com.hraczynski.trains.city.City;
import com.hraczynski.trains.city.CityRequest;
import com.hraczynski.trains.country.Country;
import com.hraczynski.trains.exceptions.definitions.ErrorDuringPropertiesCopying;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PropertiesCopierTest {

    @Test
    public void shouldCopyPropertiesWhenClassAreDifferent() {
        CityRequest cityRequest = new CityRequest()
                .setId(5L)
                .setCountry("poland")
                .setLat(3.5)
                .setLon(10)
                .setName("test");

        Country countryForTest = new Country(10L, "germany");
        City city = new City(10L, "previous", 10, 15, countryForTest);

        PropertiesCopier.copyNotNullAndNotEmptyPropertiesUsingDifferentClasses(cityRequest, city);

        assertAll(
                () -> assertEquals(cityRequest.getId(), city.getId()),
                () -> assertEquals(cityRequest.getName(), city.getName()),
                () -> assertEquals(cityRequest.getLat(), city.getLat()),
                () -> assertEquals(cityRequest.getLon(), city.getLon()),
                () -> assertEquals(countryForTest, city.getCountry())
        );
    }

    @Test
    public void shouldCopyPropertiesWhenClassAreDifferentExceptOfSomeFieldWhichAreNullOrEmpty() {
        CityRequest cityRequest = new CityRequest()
                .setId(5L)
                .setCountry(null)
                .setLat(3.5)
                .setLon(10)
                .setName("");

        Country countryForTest = new Country(10L, "germany");

        City city = new City(10L, "previous", 10, 15, countryForTest);

        PropertiesCopier.copyNotNullAndNotEmptyPropertiesUsingDifferentClasses(cityRequest, city);

        assertAll(
                () -> assertEquals(cityRequest.getId(), city.getId()),
                () -> assertNotEquals(cityRequest.getName(), city.getName()),
                () -> assertEquals(cityRequest.getLat(), city.getLat()),
                () -> assertEquals(cityRequest.getLon(), city.getLon()),
                () -> Assertions.assertEquals(countryForTest, city.getCountry())
        );
    }

    @Test
    public void shouldCopyPropertiesWhenClassAreDifferentAndSkipValuesAreProvided() {
        CityRequest cityRequest = new CityRequest()
                .setId(5L)
                .setCountry(null)
                .setLat(3.5)
                .setLon(10)
                .setName("");

        Country countryForTest = new Country(10L, "germany");

        City city = new City(10L, "previous", 10, 15, countryForTest);

        PropertiesCopier.copyNotNullAndNotEmptyPropertiesUsingDifferentClasses(cityRequest, city,"id","lat");

        assertAll(
                () -> assertNotEquals(cityRequest.getId(), city.getId()),
                () -> assertNotEquals(cityRequest.getName(), city.getName()),
                () -> assertNotEquals(cityRequest.getLat(), city.getLat()),
                () -> assertEquals(cityRequest.getLon(), city.getLon()),
                () -> Assertions.assertEquals(countryForTest, city.getCountry())
        );
    }

    @Test
    public void shouldThrowExceptionWhenInputIsNull() {
        CityRequest cityRequestNotNull = new CityRequest()
                .setId(5L)
                .setCountry(null)
                .setLat(3.5)
                .setLon(10)
                .setName("");

        Country countryForTest = new Country(10L, "germany");

        City cityNotNull = new City(10L, "previous", 10, 15, countryForTest);

        assertAll(
                () -> assertThrows(ErrorDuringPropertiesCopying.class, () -> PropertiesCopier.copyNotNullAndNotEmptyPropertiesUsingDifferentClasses(null, null)),
                () -> assertThrows(ErrorDuringPropertiesCopying.class, () -> PropertiesCopier.copyNotNullAndNotEmptyPropertiesUsingDifferentClasses(cityRequestNotNull, null)),
                () -> assertThrows(ErrorDuringPropertiesCopying.class, () -> PropertiesCopier.copyNotNullAndNotEmptyPropertiesUsingDifferentClasses(null, cityNotNull))
        );

    }
}