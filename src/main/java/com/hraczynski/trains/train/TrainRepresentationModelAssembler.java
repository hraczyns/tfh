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
public class TrainRepresentationModelAssembler extends RepresentationModelAssemblerSupport<Train, TrainDto> {
    private final ModelMapper mapper;

    @Autowired
    public TrainRepresentationModelAssembler(ModelMapper mapper) {
        super(TrainController.class, TrainDto.class);
        this.mapper = mapper;
    }

    @Override
    public TrainDto toModel(Train entity) {
        TrainDto model = instantiateModel(entity);
        mapper.map(entity, model);
        model.add(linkTo(methodOn(TrainController.class).findById(entity.getId())).withSelfRel());
        model.add(linkTo(methodOn(TrainController.class).findAll()).withRel("all"));
        model.add(linkTo(methodOn(TripController.class).getTripsByTrainId(entity.getId())).withRel("trips"));
        return model;
    }

    public CollectionModel<TrainDto> toCollectionModel(Iterable<? extends Train> entities) {
        CollectionModel<TrainDto> trainDtoS = super.toCollectionModel(entities);
        trainDtoS.add(linkTo(methodOn(TrainController.class).findAll()).withSelfRel());
        return trainDtoS;
    }
}
