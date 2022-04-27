package com.hraczynski.trains.passengers;

import com.hraczynski.trains.passengers.discount.PassengerWithDiscount;
import com.hraczynski.trains.passengers.discount.PassengerWithDiscountRequest;

import java.util.Set;

public interface PassengerService {
    Set<Passenger> getAll();

    Passenger getById(Long id);

    Passenger addPassenger(PassengerRequest request);

    Passenger deleteById(Long id);

    void update(Long id, PassengerRequest request);

    void patch(Long id, PassengerRequest request);

    PassengerWithDiscount addPassengerWhileReservation(PassengerWithDiscountRequest passengerWithDiscountRequest);
}
