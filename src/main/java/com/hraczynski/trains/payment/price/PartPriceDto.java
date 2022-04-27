package com.hraczynski.trains.payment.price;

import java.math.BigDecimal;

public record PartPriceDto(BigDecimal price, Long startStopTimeId, Long endStopTimeId) {
}
