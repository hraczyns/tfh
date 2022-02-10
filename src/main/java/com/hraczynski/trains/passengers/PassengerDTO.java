package com.hraczynski.trains.passengers;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;

@Getter
@Setter
@Accessors(chain = true)
public class PassengerDTO extends RepresentationModel<PassengerDTO> {
    private Long id;
    private String name;
    private String surname;
    private String country;
    private LocalDate bornDate;
    private String email;
}
