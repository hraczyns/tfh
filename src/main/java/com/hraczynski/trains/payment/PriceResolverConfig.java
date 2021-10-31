package com.hraczynski.trains.payment;

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
