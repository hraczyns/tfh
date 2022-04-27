package com.hraczynski.trains.payment.price;

import com.hraczynski.trains.passengers.unregistered.PassengerNotRegistered;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PricePerNogRegisteredPassenger {
    private PassengerNotRegistered passengerNotRegistered;
    private PriceResponse priceResponse;
}
