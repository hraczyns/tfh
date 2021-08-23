package com.hraczynski.trains.train;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
@ToString
public class TrainDTO extends RepresentationModel<TrainDTO> {
    private Long id;
    private TrainType model;
    private String name;
    private String representationUnique;
    private int numberOfSeats;
    private boolean used;
}
