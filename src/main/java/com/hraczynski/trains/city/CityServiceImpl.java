package com.hraczynski.trains.city;

import com.hraczynski.trains.utils.PropertiesCopier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Service;
import com.hraczynski.trains.AbstractService;
import com.hraczynski.trains.country.Country;
import com.hraczynski.trains.exceptions.definitions.EntityNotFoundException;
import com.hraczynski.trains.country.CountryRepository;

import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class CityServiceImpl extends AbstractService<City, CityRepository> implements CityService {

    private final CityRepository cityRepository;
    private final CityRepresentationModelAssembler assembler;
    private final ModelMapper mapper;
    private final CountryRepository countryRepository;

    @Override
    public CityDTO getById(Long id) {
        log.info("Looking for City with id = {}", id);
        City entityById = getEntityById(id);
        return assembler.toModel(entityById);
    }

    @Override
    public CollectionModel<CityDTO> findAll() {
        log.info("Looking for all Cities");
        Set<City> all = cityRepository.findAll();
        if (all == null || all.isEmpty()) {
            log.error("Cannot find any cities");
            throw new EntityNotFoundException(City.class, "none");
        }
        return assembler.toCollectionModel(all);
    }

    @Override
    public CityDTO save(CityRequest request) {
        checkInput(request);
        City mapped = mapper.map(request, City.class);
        mapped.setCountry(findCountryByName(request.getCountry()));
        log.info("Saving City {}", request);
        City save = cityRepository.save(mapped);
        return assembler.toModel(save);
    }

    @Override
    public CityDTO deleteById(Long id) {
        City byId = getEntityById(id);
        log.info("Deleting City with id = {}", id);
        cityRepository.deleteById(id);
        return assembler.toModel(byId);
    }

    @Override
    public CityDTO updateById(CityRequest request) {
        checkInput(request);
        getEntityById(request.getId());

        City entity = mapper.map(request, City.class);
        entity.setCountry(findCountryByName(request.getCountry()));
        log.info("Updating City {}", request);
        City saved = cityRepository.save(entity);
        return assembler.toModel(saved);
    }

    private Country findCountryByName(String country) {
        return countryRepository.findCountryByName(country)
                .orElseThrow(() -> {
                    log.error("Cannot found Country by its name {}", country);
                    return new EntityNotFoundException(Country.class, "country = " + country);
                });
    }

    @Override
    public CityDTO patchById(CityRequest request) {
        checkInput(request);
        City entity = getEntityById(request.getId());

        PropertiesCopier.copyNotNullAndNotEmptyPropertiesUsingDifferentClasses(request, entity);

        City mapped = mapper.map(entity, City.class);
        mapped.setCountry(findCountryByName(request.getCountry()));

        log.info("Patching City {}", request);
        cityRepository.save(mapped);
        return assembler.toModel(entity);
    }

}
