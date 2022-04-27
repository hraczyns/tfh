package com.hraczynski.trains.passengers.discount;

import com.hraczynski.trains.passengers.PassengerDto;
import com.hraczynski.trains.payment.Discount;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PassengerWithDiscountDto {
    private PassengerDto passenger;
    private Discount discount;
}
