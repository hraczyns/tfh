package com.hraczynski.trains.payment;

import java.math.BigDecimal;

public record PartPriceDTO(BigDecimal price, Long startStopTimeId, Long endStopTimeId) {
}
