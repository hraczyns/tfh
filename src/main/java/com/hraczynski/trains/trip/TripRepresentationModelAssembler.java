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
public class TripRepresentationModelAssembler extends RepresentationModelAssemblerSupport<Trip, TripDTO> {

    private final StopTimeMapper stopTimeMapper;

    @Autowired
    public TripRepresentationModelAssembler(StopTimeMapper stopTimeMapper) {
        super(TripController.class, TripDTO.class);
        this.stopTimeMapper = stopTimeMapper;
    }

    @Override
    public TripDTO toModel(Trip entity) {
        log.info("Transforming Trip into model");
        TripDTO tripDTO = instantiateModel(entity);
        tripDTO.setId(entity.getId());
        tripDTO.setStopTimeDTOList(stopTimeMapper.entitiesToDTOs(entity.getStopTimes()));

        tripDTO.add(linkTo(methodOn(TripController.class).getById(entity.getId())).withSelfRel());
        tripDTO.add(linkTo(methodOn(TripController.class).getAll()).withRel("all"));
        tripDTO.add(linkTo(methodOn(TrainController.class).findById(entity.getTrain().getId())).withRel("train"));

        return tripDTO;
    }

    @Override
    public CollectionModel<TripDTO> toCollectionModel(Iterable<? extends Trip> entities) {
        CollectionModel<TripDTO> tripDTOS = super.toCollectionModel(entities);
        tripDTOS.add(linkTo(methodOn(TripController.class).getAll()).withSelfRel());
        return tripDTOS;
    }
}
