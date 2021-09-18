package com.hraczynski.trains.payment;

import com.hraczynski.trains.passengers.PassengerWithDiscount;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PricePerPassenger {
    private PassengerWithDiscount passenger;
    private PriceResponse priceResponse;
}
