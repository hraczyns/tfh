package com.hraczynski.trains.passengers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PassengerRequest {
    private Long id;
    private String name;
    private String surname;
    private String email;
}
