package com.hraczynski.trains.passengers;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PassengerWithDiscount {
    private Long passengerId;
    private String discountCode;
}
