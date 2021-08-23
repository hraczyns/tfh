package com.hraczynski.trains.stoptime;

import lombok.Getter;
import lombok.Setter;
import com.hraczynski.trains.city.CityDTO;

import java.time.LocalDateTime;

@Getter
@Setter
public class StopTimeDTO {
    private Long id;
    private CityDTO cityDTO;
    private LocalDateTime arrivalTime;
    private LocalDateTime departureTime;
}
