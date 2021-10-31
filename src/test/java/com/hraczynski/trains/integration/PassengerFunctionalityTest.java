package com.hraczynski.trains.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DisplayName("Passenger integration tests")
@Tag("integration")
@ActiveProfiles("integration")
@AutoConfigureMockMvc
public class PassengerFunctionalityTest extends AbstractIntegrationTest {

    private static final String INSERT_CITY = "/static/tests_sqls/city_insert.sql";
    private static final String INSERT_COUNTRY = "/static/tests_sqls/country_insert.sql";
    private static final String API = "/api/cities";
    private static final String RESET_COUNTER = "/static/tests_sqls/reset_counter_city_id.sql";

}
