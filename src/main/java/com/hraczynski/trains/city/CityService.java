package com.hraczynski.trains.city;

import org.springframework.hateoas.CollectionModel;

public interface CityService {
    CityDTO getById(Long id);

    CollectionModel<CityDTO> findAll();

    CityDTO save(CityRequest dto);

    CityDTO deleteById(Long id);

    CityDTO updateById(CityRequest dto);

    CityDTO patchById(CityRequest dto);
}
