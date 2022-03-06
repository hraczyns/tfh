package com.hraczynski.trains.journey;

import com.hraczynski.trains.city.CityDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JourneyDto extends RepresentationModel<JourneyDto> {
    private int resultId;
    private CityDto source;
    private CityDto destination;
    private List<PartOfJourney> partOfJourneys;
}
