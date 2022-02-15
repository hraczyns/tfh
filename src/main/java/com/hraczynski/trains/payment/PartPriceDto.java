package com.hraczynski.trains.payment;

import java.math.BigDecimal;

public record PartPriceDto(BigDecimal price, Long startStopTimeId, Long endStopTimeId) {
}
