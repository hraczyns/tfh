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
    private String country;
    private LocalDate bornDate;
    private String email;
}
