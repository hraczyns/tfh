package com.hraczynski.trains.stoptime;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import com.hraczynski.trains.city.CityDTO;

import java.time.LocalDateTime;

@Getter
@Setter
public class StopTimeDTO {
    private Long id;
    private CityDTO cityDTO;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime arrivalTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime departureTime;
}
