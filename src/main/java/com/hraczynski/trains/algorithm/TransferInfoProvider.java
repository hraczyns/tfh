package com.hraczynski.trains.algorithm;

import com.hraczynski.trains.city.City;
import com.hraczynski.trains.algorithm.algorithmentities.Transfer;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public record TransferInfoProvider(Map<City, List<Transfer>> transfers) {
    public List<Transfer> getTransfersForStop(City city) {
        return transfers.getOrDefault(city, Collections.emptyList());
    }
}
