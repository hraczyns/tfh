package com.hraczynski.trains.payment.client.dto;

import com.hraczynski.trains.stoptime.StopTimeDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
public class CreatePaymentResponse {
    private String clientSecret;
    private BigDecimal price;
    private List<StopTimeDto> route;
    private List<SimplePassengerForPaymentSummaryDto> passengersInfo;
    private String mail;
    private String reservationIdentifier;
}
