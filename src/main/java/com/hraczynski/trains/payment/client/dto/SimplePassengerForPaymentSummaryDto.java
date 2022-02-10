package com.hraczynski.trains.payment.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SimplePassengerForPaymentSummaryDto {
    private String nameAndSurname;
    private String discount;
}
