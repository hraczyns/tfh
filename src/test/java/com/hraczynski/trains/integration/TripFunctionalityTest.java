package com.hraczynski.trains.integration;

import com.hraczynski.trains.city.CityRepository;
import com.hraczynski.trains.country.CountryRepository;
import com.hraczynski.trains.stoptime.StopsTimeRepository;
import com.hraczynski.trains.train.TrainRepository;
import com.hraczynski.trains.trip.TripsRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DisplayName("Trip integration tests")
@Tag("integration")
@ActiveProfiles("integration")
@AutoConfigureMockMvc
public class TripFunctionalityTest extends AbstractIntegrationTest {
    private static final String INSERT_TRIP = "/static/tests_sqls/trip_insert.sql";
    private static final String INSERT_STOP_TIME = "/static/tests_sqls/stoptime_insert.sql";
    private static final String INSERT_CITIES = "/static/tests_sqls/city_insert.sql";
    private static final String INSERT_TRAINS = "/static/tests_sqls/train_insert.sql";
    private static final String API = "/api/trips";
    private static final long EXISTING_TRIP_ID = 2L;
    private static final long EXISTING_TRAIN_ID = 21L;
    private static final long NOT_EXISTING_TRIP_ID = 2123123L;
    private static final long NOT_EXISTING_TRAIN_ID = 50L;

    @Autowired
    private TripsRepository tripsRepository;
    @Autowired
    private StopsTimeRepository stopsTimeRepository;
    @Autowired
    private TrainRepository trainRepository;
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private CountryRepository countryRepository;

    @DisplayName("Verify not null beans, profile is INTEGRATION-TEST, test database is connected and empty")
    @Test
    void verifyNotNullInjects() {
        assertThat(tripsRepository).isNotNull();
        assertThat(tripsRepository.findAll()).isEmpty();
        assertThat(stopsTimeRepository).isNotNull();
        assertThat(stopsTimeRepository.findAll()).isEmpty();
        assertThat(trainRepository).isNotNull();
        assertThat(trainRepository.findAll()).isEmpty();
        assertThat(cityRepository).isNotNull();
        assertThat(cityRepository.findAll()).isEmpty();
        assertThat(countryRepository).isNotNull();
        assertThat(countryRepository.findAll()).isEmpty();
        assertThat(env).isNotNull();
        assertThat(env.getActiveProfiles().length).isEqualTo(1);
        assertThat(env.getActiveProfiles()[0]).isEqualTo("integration");
    }

    @Nested
    @DisplayName("Positive test cases")
    class TestCases {

        @AfterEach
        void cleanAfter() {
            if ("integration".equals(env.getActiveProfiles()[0])) {
                stopsTimeRepository.deleteAll();
                tripsRepository.deleteAll();
                trainRepository.deleteAll();
                cityRepository.deleteAll();
                countryRepository.deleteAll();
            } else {
                throw new IllegalStateException("This is not integration-test environment!");
            }
        }

        @DisplayName("Find all")
        @Test
        @Sql(scripts = {
                INSERT_CITIES, INSERT_TRAINS, INSERT_TRIP, INSERT_STOP_TIME
        })
        void findAll() throws Exception {
            simpleVerifyGet(API + "/all", "trips.json");
        }

        @DisplayName("Find by id")
        @Test
        @Sql(scripts = {
                INSERT_CITIES, INSERT_TRAINS, INSERT_TRIP, INSERT_STOP_TIME
        })
        void findById() throws Exception {
            simpleVerifyGet(API + "/" + EXISTING_TRIP_ID, "trip_one.json");
        }

        @DisplayName("Find by train id")
        @Test
        @Sql(scripts = {
                INSERT_CITIES, INSERT_TRAINS, INSERT_TRIP, INSERT_STOP_TIME
        })
        void findByTrainId() throws Exception {
            simpleVerifyGet(API + "?train_id=" + EXISTING_TRAIN_ID, "trip_by_train_id.json");
        }
    }

    @Nested
    @DisplayName("Negative test cases")
    class NegativeTestCases {
        @AfterEach
        void cleanAfter() {
            if ("integration".equals(env.getActiveProfiles()[0])) {
                stopsTimeRepository.deleteAll();
                tripsRepository.deleteAll();
                trainRepository.deleteAll();
                cityRepository.deleteAll();
                countryRepository.deleteAll();
            } else {
                throw new IllegalStateException("This is not integration-test environment!");
            }
        }

        @DisplayName("Does not found all - 0 entities")
        @Test
        void notFindAll() throws Exception {
            simpleVerifyGet(API + "/all", "0trip_found.json", NOT_FOUND);
        }

        @DisplayName("Does not found by id")
        @Test
        void notFindById() throws Exception {
            simpleVerifyGet(API + "/" + NOT_EXISTING_TRIP_ID, "no_trip_found_by_id.json", NOT_FOUND);
        }

        @DisplayName("Does not found by train id")
        @Test
        void notFindByTrainId() throws Exception {
            simpleVerifyGet(API + "?train_id=" + NOT_EXISTING_TRAIN_ID, "no_trip_found_by_train_id.json", NOT_FOUND);
        }
    }

}
