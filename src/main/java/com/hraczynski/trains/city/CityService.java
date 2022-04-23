package com.hraczynski.trains.city;

import java.util.Set;

public interface CityService {
    City findById(Long id);

    Set<City> findAll();

    City save(CityRequest cityRequest);

    City deleteById(Long id);

    void update(Long id, CityRequest cityRequest);

    void patch(Long id, CityRequest cityRequest);
}
