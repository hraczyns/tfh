package com.hraczynski.trains.passengers;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
@Getter
@Setter
@ToString
public class PassengerRequest {
    private Long id;
    private String name;
    private String surname;
    private String gender;
    private String country;
    private String notes;
    private LocalDate bornDate;
}
