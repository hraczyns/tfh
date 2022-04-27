package com.hraczynski.trains.payment.price;

import com.hraczynski.trains.passengers.discount.PassengerWithDiscountRequest;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PricePerPassenger {
    private PassengerWithDiscountRequest passenger;
    private PriceResponse priceResponse;
}
