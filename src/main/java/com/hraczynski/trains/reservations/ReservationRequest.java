package com.hraczynski.trains.reservations;

import com.hraczynski.trains.passengers.PassengerNotRegistered;
import com.hraczynski.trains.passengers.PassengerWithDiscountRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
public class ReservationRequest {
    private Long id;
    private List<Long> reservedRoute;
    private Set<PassengerWithDiscountRequest> idPassengersWithDiscounts;
    private List<PassengerNotRegistered> passengerNotRegisteredList;
}
