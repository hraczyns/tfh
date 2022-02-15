package com.hraczynski.trains.passengers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PassengerRepresentationModelAssembler extends RepresentationModelAssemblerSupport<Passenger, PassengerDto> {

    private final ModelMapper mapper;

    @Autowired
    public PassengerRepresentationModelAssembler(ModelMapper mapper) {
        super(PassengerController.class, PassengerDto.class);
        this.mapper = mapper;
    }

    @Override
    public PassengerDto toModel(Passenger entity) {
        PassengerDto model = instantiateModel(entity);
        mapper.map(entity, model);

        model.add(linkTo(methodOn(PassengerController.class).getById(entity.getId())).withSelfRel());
        return model;
    }

    @Override
    public CollectionModel<PassengerDto> toCollectionModel(Iterable<? extends Passenger> entities) {
        CollectionModel<PassengerDto> passengerDtos = super.toCollectionModel(entities);
        passengerDtos.add(linkTo(methodOn(PassengerController.class).getAll()).withSelfRel());
        return passengerDtos;
    }
}
