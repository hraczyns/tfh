package com.hraczynski.trains;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

//@SpringBootApplication(scanBasePackages = { "com.hraczynski.trains","com.hraczynski.trains.city","com.hraczynski.trains.country","com.hraczynski.trains.passengers","com.hraczynski.trains.payment","com.hraczynski.trains.city.CityService.java","com.hraczynski.trains.reservations","com.hraczynski.trains.routefinder"})
@SpringBootApplication
public class TrainManagementApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(TrainManagementApplication.class, args);
    }

}
