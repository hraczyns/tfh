package com.hraczynski.trains;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

//@SpringBootApplication(scanBasePackages = { "pl.hub.flights","pl.hub.flights.controllers","pl.hub.flights.entities","pl.hub.flights.hateoasmodels","pl.hub.flights.repositories","pl.hub.flights.services","pl.hub.flights.utils"})
@SpringBootApplication(scanBasePackages = {"com.hraczynski.trains"})
public class TrainManagementApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(TrainManagementApplication.class, args);
    }

}
