package com.hraczynski.trains.city;

import com.hraczynski.trains.AbstractService;
import com.hraczynski.trains.country.Country;
import com.hraczynski.trains.country.CountryRepository;
import com.hraczynski.trains.exceptions.definitions.EntityNotFoundException;
import com.hraczynski.trains.utils.PropertiesCopier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class CityServiceImpl extends AbstractService<City, CityRepository> implements CityService {

    private final CityRepository cityRepository;
    private final ModelMapper mapper;
    private final CountryRepository countryRepository;

    @Override
    public City findById(Long id) {
        log.info("Looking for City with id = {}", id);
        return getEntityById(id);
    }

    @Override
    public Set<City> findAll() {
        log.info("Looking for all Cities");
        Set<City> all = cityRepository.findAll();
        if (all == null || all.isEmpty()) {
            log.error("Cannot find any cities");
            throw new EntityNotFoundException(City.class, "none");
        }
        return all;
    }

    @Override
    public City save(CityRequest request) {
        checkInput(request);
        City mapped = mapper.map(request, City.class);
        mapped.setCountry(findCountryByName(request.getCountry()));
        log.info("Saving City");
        return cityRepository.save(mapped);
    }

    @Override
    public City deleteById(Long id) {
        City byId = getEntityById(id);
        log.info("Deleting City with id = {}", id);
        cityRepository.deleteById(id);
        return byId;
    }

    @Override
    public void update(CityRequest request) {
        checkInput(request);
        getEntityById(request.getId());

        City entity = mapper.map(request, City.class);
        entity.setCountry(findCountryByName(request.getCountry()));
        log.info("Updating City");
        cityRepository.save(entity);
    }

    private Country findCountryByName(String country) {
        return countryRepository.findCountryByName(country)
                .orElseThrow(() -> {
                    log.error("Cannot found Country by its name {}", country);
                    return new EntityNotFoundException(Country.class, "country = " + country);
                });
    }

    @Override
    public void patch(CityRequest request) {
        checkInput(request);
        City entity = getEntityById(request.getId());

        PropertiesCopier.copyNotNullAndNotEmptyPropertiesUsingDifferentClasses(request, entity);
        String country = request.getCountry();

        if (!StringUtils.isEmpty(country))
            entity.setCountry(findCountryByName(country));

        log.info("Patching City");
        cityRepository.save(entity);
    }

}
