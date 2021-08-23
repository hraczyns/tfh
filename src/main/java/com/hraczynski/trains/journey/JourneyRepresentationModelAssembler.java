package com.hraczynski.trains.journey;

import com.hraczynski.trains.algorithm.algorithmentities.Journey;
import com.hraczynski.trains.algorithm.algorithmentities.TimetableRouteSection;
import com.hraczynski.trains.algorithm.algorithmentities.Transfer;
import com.hraczynski.trains.city.City;
import com.hraczynski.trains.city.CityRepresentationModelAssembler;
import com.hraczynski.trains.routefinder.RouteFinderController;
import com.hraczynski.trains.stoptime.StopTime;
import com.hraczynski.trains.stoptime.StopTimeDTO;
import com.hraczynski.trains.stoptime.StopTimeMapper;
import com.hraczynski.trains.train.Train;
import com.hraczynski.trains.train.TrainDTO;
import com.hraczynski.trains.train.TrainRepresentationModelAssembler;
import com.hraczynski.trains.trip.Trip;
import com.hraczynski.trains.trip.TripController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
@Slf4j
public class JourneyRepresentationModelAssembler extends RepresentationModelAssemblerSupport<Journey, JourneyDTO> {
    private final CityRepresentationModelAssembler cityRepresentationModelAssembler;
    private final TrainRepresentationModelAssembler trainRepresentationModelAssembler;
    private final StopTimeMapper stopTimeMapper;

    @Autowired
    public JourneyRepresentationModelAssembler(CityRepresentationModelAssembler cityRepresentationModelAssembler, TrainRepresentationModelAssembler trainRepresentationModelAssembler, StopTimeMapper stopTimeMapper) {
        super(RouteFinderController.class, JourneyDTO.class);
        this.cityRepresentationModelAssembler = cityRepresentationModelAssembler;
        this.trainRepresentationModelAssembler = trainRepresentationModelAssembler;
        this.stopTimeMapper = stopTimeMapper;
    }

    @Override
    public JourneyDTO toModel(Journey entity) {
        log.info("Transforming Journey {} into model", entity);
        JourneyDTO dto = instantiateModel(entity);
        entity.sections().forEach(s -> {
            City source = s.getSource();
            City destination = s.getDestination();
            Trip trip;
            if (s instanceof TimetableRouteSection) {
                trip = ((TimetableRouteSection) s).getTrip();
                if (trip != null) {
                    Train train = trip.getTrain();
                    List<StopTime> collected = trip.getStopTimes().stream()
                            .filter(stopTime -> stopTime.getStop().getId().equals(source.getId()) || stopTime.getStop().getId().equals(destination.getId()))
                            .collect(Collectors.toList());
                    if (collected.size() == 2) {
                        TrainDTO trainDTO = trainRepresentationModelAssembler.toModel(train);
                        trainDTO.setUsed(true);
                        PartOfJourney partOfJourney = new PartOfJourneyTimeTable(
                                stopTimeMapper.entityToDTO(collected.get(0)),
                                stopTimeMapper.entityToDTO(collected.get(1)),
                                trainDTO
                        );
                        dto.getPartOfJourneys().add(partOfJourney);
                        dto.add(linkTo(methodOn(TripController.class).getById(trip.getId())).withSelfRel());
                    }
                }
            } else {
                dto.getPartOfJourneys().add((Transfer) s);
            }
        });
        return dto;
    }

    @Override
    protected JourneyDTO instantiateModel(Journey entity) {
        JourneyDTO dto = super.instantiateModel(entity);
        dto.setPartOfJourneys(new ArrayList<>());
        dto.setSource(cityRepresentationModelAssembler.toModel(entity.sections().get(0).getSource()));
        dto.setDestination(cityRepresentationModelAssembler.toModel(entity.sections().get(entity.sections().size() - 1).getDestination()));
        dto.setResultId(entity.resultId());
        return dto;
    }

    public record PartOfJourneyTimeTable(StopTimeDTO start, StopTimeDTO end, TrainDTO train) implements PartOfJourney {
    }

    public interface PartOfJourney {
    }

}