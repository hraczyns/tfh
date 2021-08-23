package com.hraczynski.trains.payment;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PriceResponse {
    private BigDecimal priceInGeneral;
    private List<PricePartResponse> prices;
}
