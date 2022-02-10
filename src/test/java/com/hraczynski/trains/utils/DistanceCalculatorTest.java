package com.hraczynski.trains.utils;

import com.hraczynski.trains.city.City;
import com.hraczynski.trains.city.CityRequest;
import com.hraczynski.trains.country.Country;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Distance calculations tests")
public class DistanceCalculatorTest {

    private final DistanceCalculator distanceCalculator = new DistanceCalculator();

    @DisplayName("Calculation with city tests")
    @ParameterizedTest(name = "Test n.{index}")
    @MethodSource("provideCities")
    void calculateWithCities(City value1, City value2, Double result) {
        assertThat(String.format("%.6f", distanceCalculator.calculate(value1, value2))).isEqualTo(String.format("%.6f", result));
    }

    @DisplayName("Calculation with city request tests")
    @ParameterizedTest(name = "Test n.{index}")
    @MethodSource("provideCityRequests")
    void calculateWithCityRequests(CityRequest value1, CityRequest value2, Double result) {
        assertThat(String.format("%.6f", distanceCalculator.calculate(value1, value2))).isEqualTo(String.format("%.6f", result));
    }


    private static Stream<Arguments> provideCities() {
        return Stream.of(
                Arguments.of(new City(1L, "random", -20.3, 50.4, new Country()), new City(1L, "random", 100.3, 0.4, new Country()), 12080.047145),
                Arguments.of(new City(1L, "random", 10.3, 50.4, new Country()), new City(1L, "random", -50.12, 0.0, new Country()), 7970.468245),
                Arguments.of(new City(1L, "random", 0.0, 0.0, new Country()), new City(1L, "random", 0.0, 0.0, new Country()), 0.0)
        );
    }

    private static Stream<Arguments> provideCityRequests() {
        return Stream.of(
                Arguments.of(new CityRequest().setLon(-20.3).setLat(50.4), new CityRequest().setLon(100.3).setLat(0.4), 12080.047145),
                Arguments.of(new CityRequest().setLon(10.3).setLat(50.4), new CityRequest().setLon(-50.12).setLat(0.0), 7970.468245),
                Arguments.of(new CityRequest().setLon(0.0).setLat(0.0), new CityRequest().setLon(0.0).setLat(0.0), 0.0)
        );
    }
}
