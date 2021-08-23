package com.hraczynski.trains.journey;

import com.hraczynski.trains.city.CityDTO;
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
public class JourneyDTO extends RepresentationModel<JourneyDTO> {
    private int resultId;
    private CityDTO source;
    private CityDTO destination;
    private List<JourneyRepresentationModelAssembler.PartOfJourney> partOfJourneys;
}
