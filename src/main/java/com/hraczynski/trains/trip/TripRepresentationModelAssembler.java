package com.hraczynski.trains.trip;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;
import com.hraczynski.trains.train.TrainController;
import com.hraczynski.trains.stoptime.StopTimeMapper;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
@Slf4j
public class TripRepresentationModelAssembler extends RepresentationModelAssemblerSupport<Trip, TripDto> {

    private final StopTimeMapper stopTimeMapper;

    @Autowired
    public TripRepresentationModelAssembler(StopTimeMapper stopTimeMapper) {
        super(TripController.class, TripDto.class);
        this.stopTimeMapper = stopTimeMapper;
    }

    @Override
    public TripDto toModel(Trip entity) {
        log.info("Transforming Trip into model");
        TripDto tripDto = instantiateModel(entity);
        tripDto.setId(entity.getId());
        tripDto.setStopTimeDtoList(stopTimeMapper.entitiesToDtos(entity.getStopTimes()));

        tripDto.add(linkTo(methodOn(TripController.class).getById(entity.getId())).withSelfRel());
        tripDto.add(linkTo(methodOn(TripController.class).getAll()).withRel("all"));
        tripDto.add(linkTo(methodOn(TrainController.class).findById(entity.getTrain().getId())).withRel("train"));

        return tripDto;
    }

    @Override
    public CollectionModel<TripDto> toCollectionModel(Iterable<? extends Trip> entities) {
        CollectionModel<TripDto> tripDtoS = super.toCollectionModel(entities);
        tripDtoS.add(linkTo(methodOn(TripController.class).getAll()).withSelfRel());
        return tripDtoS;
    }
}
