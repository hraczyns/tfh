package com.hraczynski.trains.payment.price;

import com.hraczynski.trains.payment.Discount;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class PriceWithDiscount {
    private BigDecimal initialPrice;
    private BigDecimal priceWithDiscount;
    private Discount discount;
}
