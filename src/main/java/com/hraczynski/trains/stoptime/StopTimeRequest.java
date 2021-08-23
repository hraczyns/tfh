package com.hraczynski.trains.stoptime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import com.hraczynski.trains.city.CityRequest;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class StopTimeRequest {
    private Long id;
    private CityRequest cityRequest;
    private LocalDateTime arrivalTime;
    private LocalDateTime departureTime;
}
