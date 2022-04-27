package com.hraczynski.trains.payment.price;

import com.hraczynski.trains.payment.Discount;

import java.math.BigDecimal;
import java.util.List;


public record PriceDto(Long passengerId, String name, String surname,
                       BigDecimal price, Discount discount,
                       List<PartPriceDto> partPrice) {
}
