package com.hraczynski.trains.payment;

import java.math.BigDecimal;
import java.util.List;


public record PriceDTO(Long passengerId, String name, String surname,
                       BigDecimal price, Discount discount,
                       List<PartPriceDTO> partPrice) {
}
