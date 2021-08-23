package com.hraczynski.trains.passengers;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;

@Getter
@Setter
public class PassengerDTO extends RepresentationModel<PassengerDTO> {
    private Long id;
    private String name;
    private String surname;
    private String gender;
    private String country;
    private String notes;
    private LocalDate bornDate;
}
