package com.hraczynski.trains.journey;

import com.hraczynski.trains.stoptime.StopTimeDto;
import com.hraczynski.trains.train.TrainDto;

public record PartOfJourneyTimeTable(StopTimeDto start, StopTimeDto end, TrainDto train) implements PartOfJourney {
}
