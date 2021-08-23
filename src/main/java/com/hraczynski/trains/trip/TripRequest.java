package com.hraczynski.trains.trip;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import com.hraczynski.trains.stoptime.StopTimeRequest;

import java.util.List;

@Getter
@Setter
@ToString
public class TripRequest {
    private Long id;
    private List<StopTimeRequest> stopTimeDTOList;
}
