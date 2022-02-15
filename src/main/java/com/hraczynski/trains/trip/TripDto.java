package com.hraczynski.trains.trip;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import com.hraczynski.trains.stoptime.StopTimeDto;

import java.util.List;

@Getter
@Setter
public class TripDto extends RepresentationModel<TripDto> {
    private Long id;
    private List<StopTimeDto> stopTimeDtoList;
}
