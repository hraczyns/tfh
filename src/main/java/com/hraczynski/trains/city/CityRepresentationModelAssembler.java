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
public class CityRepresentationModelAssembler extends RepresentationModelAssemblerSupport<City, CityDTO> {

    private final ModelMapper mapper;

    public CityRepresentationModelAssembler(ModelMapper mapper) {
        super(CityController.class, CityDTO.class);
        this.mapper = mapper;
    }

    @Override
    public CityDTO toModel(City entity) {
        log.info("Transforming City into model");
        CityDTO cityDTO = instantiateModel(entity);
        mapper.map(entity, cityDTO);
        cityDTO.setCountry(entity.getCountry().getName());

        cityDTO.add(linkTo(methodOn(CityController.class).findById(entity.getId())).withSelfRel());
        return cityDTO;
    }

    @Override
    public CollectionModel<CityDTO> toCollectionModel(Iterable<? extends City> entities) {
        CollectionModel<CityDTO> cityDTOS = super.toCollectionModel(entities);
        cityDTOS.add(linkTo(methodOn(CityController.class).findAll()).withSelfRel());
        return cityDTOS;
    }
}
