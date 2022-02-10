package com.hraczynski.trains.payment.client.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public final class CreatePayment {
    @SerializedName("items")
    Object[] items;
}
