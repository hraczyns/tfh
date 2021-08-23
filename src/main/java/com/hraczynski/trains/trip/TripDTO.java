package com.hraczynski.trains.trip;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import com.hraczynski.trains.stoptime.StopTimeDTO;

import java.util.List;

@Getter
@Setter
public class TripDTO extends RepresentationModel<TripDTO> {
    private Long id;
    private List<StopTimeDTO> stopTimeDTOList;
}
