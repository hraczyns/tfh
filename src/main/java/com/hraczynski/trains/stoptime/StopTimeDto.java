package com.hraczynski.trains.stoptime;

import com.hraczynski.trains.city.CityDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class StopTimeDto {
    private Long id;
    private CityDto cityDto;
    private LocalDateTime arrivalTime;
    private LocalDateTime departureTime;
}
