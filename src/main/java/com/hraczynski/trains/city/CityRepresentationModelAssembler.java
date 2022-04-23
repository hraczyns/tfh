package com.hraczynski.trains.city;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
@Slf4j
public class CityRepresentationModelAssembler extends RepresentationModelAssemblerSupport<City, CityDto> {

    private final ModelMapper mapper;

    public CityRepresentationModelAssembler(ModelMapper mapper) {
        super(CityController.class, CityDto.class);
        this.mapper = mapper;
    }

    @Override
    public CityDto toModel(City entity) {
        CityDto cityDto = instantiateModel(entity);
        mapper.map(entity, cityDto);
        cityDto.setCountry(entity.getCountry().getName());
        cityDto.add(linkTo(methodOn(CityController.class).findById(entity.getId())).withSelfRel());
        return cityDto;
    }

    @Override
    public CollectionModel<CityDto> toCollectionModel(Iterable<? extends City> entities) {
        CollectionModel<CityDto> cityDtoS = super.toCollectionModel(entities);
        cityDtoS.add(linkTo(methodOn(CityController.class).findAll()).withSelfRel());
        return cityDtoS;
    }
}
