package com.hraczynski.trains.train;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TrainRequest {
    private Long id;
    private TrainType model;
    private String name;
    private String representationUnique;
    private int numberOfSeats;
}
