package com.hraczynski.trains.payment;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PricePartResponse {
    private Long startId;
    private Long stopId;
    private BigDecimal price;
}
