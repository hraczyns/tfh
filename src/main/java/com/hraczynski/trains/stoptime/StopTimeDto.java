package com.hraczynski.trains.stoptime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hraczynski.trains.city.CityDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class StopTimeDto {
    private Long id;
    private CityDto cityDto;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime arrivalTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime departureTime;
}
