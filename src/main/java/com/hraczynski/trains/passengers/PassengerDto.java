package com.hraczynski.trains.passengers;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
@Accessors(chain = true)
public class PassengerDto extends RepresentationModel<PassengerDto> {
    private Long id;
    private String name;
    private String surname;
    private String email;
}
