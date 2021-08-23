package com.hraczynski.trains.train;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TrainType {
    NORMAL(1, 200),
    FAST(1.2,100),
    PREMIUM(1.35, 100),
    ECONOMIC(0.8, 400);

    private final double priceRatio;
    private final int numberOfSeats;
}

