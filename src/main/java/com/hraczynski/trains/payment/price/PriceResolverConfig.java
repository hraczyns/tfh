package com.hraczynski.trains.payment.price;

import com.hraczynski.trains.payment.Discount;
import com.hraczynski.trains.stoptime.StopTimeRequest;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder(setterPrefix = "with")
@Getter
public class PriceResolverConfig {
    private List<StopTimeRequest> stopTimeRequests;
    private List<Long> stopTimeIds;
    private Discount discount;
}
