package com.hraczynski.trains.stoptime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StopTimeExtendedDto {
    private StopTimeDto stopTimeDto;
    private StopTimeDto nextStopTimeDto;
    private Long tripId;
    private Long trainId;
    private String trainUnique;
    private String trainClass;
}
