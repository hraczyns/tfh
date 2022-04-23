package com.hraczynski.trains.payment;

import com.hraczynski.trains.passengers.PassengerWithDiscountRequest;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PricePerPassenger {
    private PassengerWithDiscountRequest passenger;
    private PriceResponse priceResponse;
}
