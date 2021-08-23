package com.hraczynski.trains.train;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;
import com.hraczynski.trains.trip.TripController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
@Slf4j
public class TrainRepresentationModelAssembler extends RepresentationModelAssemblerSupport<Train, TrainDTO> {
    private final ModelMapper mapper;

    @Autowired
    public TrainRepresentationModelAssembler(ModelMapper mapper) {
        super(TrainController.class, TrainDTO.class);
        this.mapper = mapper;
    }

    @Override
    public TrainDTO toModel(Train entity) {
        log.info("Transforming Train {} into model",entity);
        TrainDTO model = instantiateModel(entity);
        mapper.map(entity, model);
        model.add(linkTo(methodOn(TrainController.class).getById(entity.getId())).withSelfRel());
        model.add(linkTo(methodOn(TrainController.class).getAll()).withRel("all"));
        model.add(linkTo(methodOn(TripController.class).getTripsByTrainId(entity.getId())).withRel("trips"));
        return model;
    }

    public CollectionModel<TrainDTO> toCollectionModel(Iterable<? extends Train> entities) {
        CollectionModel<TrainDTO> flightDTOS = super.toCollectionModel(entities);
        flightDTOS.add(linkTo(methodOn(TrainController.class).getAll()).withSelfRel());
        return flightDTOS;
    }
}
