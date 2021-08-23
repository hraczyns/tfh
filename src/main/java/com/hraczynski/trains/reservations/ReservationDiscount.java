package com.hraczynski.trains.reservations;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ReservationDiscount {
    STUDENT(50),
    VETERAN(90);

    private final double value;
}
