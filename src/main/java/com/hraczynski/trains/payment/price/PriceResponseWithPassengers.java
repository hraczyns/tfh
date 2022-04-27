package com.hraczynski.trains.payment.price;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

@Data
@Accessors(chain = true)
public class PriceResponseWithPassengers {
    private Set<PricePerPassenger> priceResponseForPassengersIds;
    private Set<PricePerNogRegisteredPassenger> pricePerNogRegisteredPassengers;
}
