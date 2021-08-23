package com.hraczynski.trains.city;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class CityRequest {
    private Long id;
    private String name;
    private String country;
    private double lon;
    private double lat;
}
