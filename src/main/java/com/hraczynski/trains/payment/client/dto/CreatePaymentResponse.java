package com.hraczynski.trains.payment.client.dto;

import com.hraczynski.trains.stoptime.StopTimeDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
public class CreatePaymentResponse {
    private String clientSecret;
    private BigDecimal price;
    private List<StopTimeDTO> route;
    private List<SimplePassengerForPaymentSummaryDto> passengersInfo;
    private String mail;
}
