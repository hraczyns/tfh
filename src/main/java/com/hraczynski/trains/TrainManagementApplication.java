package com.hraczynski.trains;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class TrainManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(TrainManagementApplication.class, args);
    }
}

