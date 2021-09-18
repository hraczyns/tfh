package com.hraczynski.trains.payment;

import com.hraczynski.trains.passengers.PassengerNotRegistered;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PricePerNogRegisteredPassenger {
    private PassengerNotRegistered passengerNotRegistered;
    private PriceResponse priceResponse;
}
