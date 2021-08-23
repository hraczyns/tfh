package com.hraczynski.trains.algorithm;

import com.hraczynski.trains.stoptime.StopTime;

import java.util.List;

public record StopInfoProvider(List<StopTime> stopTimeList) { }
