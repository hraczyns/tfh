package com.hraczynski.trains.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum Discount {
    PUPIL(33, "P"),
    STUDENT(50, "S"),
    VETERAN(90, "V");

    private final double value;
    private final String code;

    public static Discount findByCode(String code) {
        return Arrays.stream(values())
                .filter(s -> s.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }
}
