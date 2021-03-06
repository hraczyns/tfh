package com.hraczynski.trains.city;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CityDto extends RepresentationModel<CityDto> {
    private Long id;
    private String name;
    private String country;
    private double lon;
    private double lat;
}
