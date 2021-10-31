package com.hraczynski.trains.integration;

import com.hraczynski.trains.city.CityDTO;
import com.hraczynski.trains.city.CityRepository;
import com.hraczynski.trains.city.CityRequest;
import com.hraczynski.trains.country.CountryRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.stream.Stream;

import static com.hraczynski.trains.builders.CityTestBuilder.BasicCityRequest;
import static com.hraczynski.trains.builders.CityTestBuilder.EmptyCityRequestWithOnlyId;
import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DisplayName("City integration tests")
@Tag("integration")
@ActiveProfiles("integration")
@AutoConfigureMockMvc
public class CityFunctionalityTest extends AbstractIntegrationTest {

    private static final String INSERT_CITY = "/static/tests_sqls/city_insert.sql";
    private static final String INSERT_COUNTRY = "/static/tests_sqls/country_insert.sql";
    private static final String API = "/api/cities";
    private static final String RESET_COUNTER = "/static/tests_sqls/reset_counter_city_id.sql";

    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private CountryRepository countryRepository;

    @DisplayName("Verify not null beans, profile is INTEGRATION-TEST, test database is connected and empty")
    @Test
    void verifyNotNullInjects() {
        assertThat(cityRepository).isNotNull();
        assertThat(countryRepository).isNotNull();
        assertThat(cityRepository.findAll()).isEmpty();
        assertThat(countryRepository.findAll()).isEmpty();
        assertThat(env).isNotNull();
        assertThat(env.getActiveProfiles().length).isEqualTo(1);
        assertThat(env.getActiveProfiles()[0]).isEqualTo("integration");
    }

    @Nested
    @DisplayName("Positive test cases")
    class TestCases {
        private static final long EXISTING_CITY_ID = 2L;
        private static final long NOT_EXISTING_CITY_ID = 2123123L;

        @AfterEach
        void cleanAfter() {
            if ("integration".equals(env.getActiveProfiles()[0])) {
                if (!cityRepository.findAll().isEmpty()) {
                    cityRepository.deleteAll();
                }
                if (countryRepository.findAll().iterator().hasNext()) {
                    countryRepository.deleteAll();
                }
            } else {
                throw new IllegalStateException("This is not integration-test environment!");
            }
        }

        @DisplayName("Find all")
        @Test
        @Sql(scripts = INSERT_CITY)
        void findAll() throws Exception {
            simpleVerifyGet(API + "/all", "cities.json");
        }

        @DisplayName("Find by id")
        @Test
        @Sql(scripts = INSERT_CITY)
        void findById() throws Exception {
            simpleVerifyGet(API + "/" + EXISTING_CITY_ID, "city_one.json");
        }

        @DisplayName("Save test")
        @Test
        @Sql(scripts = {INSERT_COUNTRY, RESET_COUNTER})
        void save() throws Exception {
            CityRequest city = prepareCityRequest();
            simpleVerifyPost(API, "created_city.json", city);
        }

        @DisplayName("Delete test")
        @Test
        @Sql(scripts = INSERT_CITY)
        void deleteById() throws Exception {
            simpleVerifyDelete(API + "/" + EXISTING_CITY_ID, "city_one.json");
        }

        @DisplayName("Update test")
        @Test
        @Sql(scripts = INSERT_CITY)
        void update() throws Exception {
            CityRequest city = prepareCityRequest();
            simpleVerifyPut(API, city, city.getId(), CityDTO.class);

        }

        @DisplayName("Patch tests")
        @ParameterizedTest(name = "Patch test with input combinations n.{index}")
        @MethodSource("provide")
        @Sql(scripts = INSERT_CITY)
        void patchTest(Double lat, Double lon, String name, String countryName) throws Exception {
            CityRequest city = prepareCityRequest(lat, lon, name, countryName);
            CityDTO cityBeforeChangingAnything = instantiate(findByIdJson(API + "/" + city.getId()), CityDTO.class);
            mvc.perform(patch(API)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(mapper.writeValueAsBytes(city)))
                    .andExpect(status().isNoContent());
            CityDTO cityAfterRequest = instantiate(findByIdJson(API + "/" + city.getId()), CityDTO.class);

            verifyPatch(city, cityBeforeChangingAnything, cityAfterRequest);
        }

        private static Stream<Arguments> provide() {
            return Stream.of(
                    Arguments.of(10.2, -2.0, "Warsaw", "Poland"),
                    Arguments.of(-3.0, null, null, null),
                    Arguments.of(null, null, "Berlin", null)
            );
        }
    }

    @Nested
    @DisplayName("Negative test cases")
    class NegativeTestCases {
        private static final long EXISTING_CITY_ID = 2L;
        private static final long NOT_EXISTING_CITY_ID = 2123123L;

        @AfterEach
        void cleanAfter() {
            if ("integration".equals(env.getActiveProfiles()[0])) {
                if (!cityRepository.findAll().isEmpty()) {
                    cityRepository.deleteAll();
                }
                if (countryRepository.findAll().iterator().hasNext()) {
                    countryRepository.deleteAll();
                }
            } else {
                throw new IllegalStateException("This is not integration-test environment!");
            }
        }

//        @DisplayName("Not find all - 0 entities")
//        @Test
//        void findAll() throws Exception {
//            simpleVerifyGet(API + "/all", "cities.json");
//        }
//
//        @DisplayName("Find by id")
//        @Test
//        void findById() throws Exception {
//            simpleVerifyGet(API + "/" + EXISTING_CITY_ID, "city_one.json");
//        }


    }

    private CityRequest prepareCityRequest() {
        return make(a(BasicCityRequest));
    }

    private CityRequest prepareCityRequest(Double lat, Double lon, String name, String countryName) {
        CityRequest cityRequest = make(a(EmptyCityRequestWithOnlyId));
        if (lat != null) {
            cityRequest.setLat(lat);
        }
        if (lon != null) {
            cityRequest.setLon(lon);
        }
        if (name != null && !name.isEmpty()) {
            cityRequest.setName(name);
        }
        if (countryName != null) {
            cityRequest.setCountry(countryName);
        }
        return cityRequest;
    }
}
