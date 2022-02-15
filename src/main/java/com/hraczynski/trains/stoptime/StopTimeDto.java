package com.hraczynski.trains.stoptime;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import com.hraczynski.trains.city.CityDto;

import java.time.LocalDateTime;

@Getter
@Setter
public class StopTimeDto {
    private Long id;
    private CityDto cityDto;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime arrivalTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime departureTime;
}
