package com.hraczynski.trains.passengers;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PassengerNotRegistered {
    private String name;
    private String surname;
    private String discountCode;
}
