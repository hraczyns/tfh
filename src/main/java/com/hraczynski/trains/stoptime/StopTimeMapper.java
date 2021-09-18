package com.hraczynski.trains.stoptime;

import com.hraczynski.trains.city.City;
import com.hraczynski.trains.city.CityDTO;
import com.hraczynski.trains.city.CityRepository;
import com.hraczynski.trains.city.CityRepresentationModelAssembler;
import com.hraczynski.trains.exceptions.definitions.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public record StopTimeMapper(
        CityRepresentationModelAssembler cityRepresentationModelAssembler, CityRepository cityRepository,
        StopsTimeRepository stopsTimeRepository,
        ModelMapper mapper) {


    public StopTimeDTO entityToDTO(StopTime stopTime) {
        log.info("Mapping StopTime to StopTimeDTO");
        StopTimeDTO stopTimeDTO = new StopTimeDTO();
        stopTimeDTO.setId(stopTime.getId());
        stopTimeDTO.setCityDTO(cityRepresentationModelAssembler.toModel(stopTime.getStop()));
        stopTimeDTO.setArrivalTime(stopTime.getArrivalTime());
        stopTimeDTO.setDepartureTime(stopTime.getDepartureTime());
        return stopTimeDTO;
    }

    public StopTime requestToEntity(StopTimeRequest stopTimeRequest) {
        log.info("Mapping StopTimeRequest to StopTime");
        StopTime stopTime = new StopTime();
        City city = findCityById(stopTimeRequest.getCityId());
        stopTime.setStop(city);
        stopTime.setId(stopTimeRequest.getId());
        stopTime.setArrivalTime(stopTimeRequest.getArrivalTime());
        stopTime.setDepartureTime(stopTimeRequest.getDepartureTime());
        return stopTime;
    }

    private City findCityById(Long id) {
        return cityRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Cannot find City with id = " + id);
                    return new EntityNotFoundException(City.class, "id = " + id);
                });
    }

    public StopTimeDTO requestToDTO(StopTimeRequest stopTimeRequest) {
        log.info("Mapping StopTimeRequest to StopTimeDTO");

        City city = findCityById(stopTimeRequest.getCityId());
        CityDTO cityDTO = mapper.map(city, CityDTO.class);

        StopTimeDTO stopTimeDTO = new StopTimeDTO();
        stopTimeDTO.setId(stopTimeRequest.getId());
        stopTimeDTO.setCityDTO(cityDTO);
        stopTimeDTO.setArrivalTime(stopTimeRequest.getArrivalTime());
        stopTimeDTO.setDepartureTime(stopTimeRequest.getDepartureTime());
        return stopTimeDTO;
    }

    public StopTime idToEntity(Long id) {
        return stopsTimeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Cannot find StopTime with id = " + id);
                    return new EntityNotFoundException(StopTime.class, "id = " + id);
                });
    }

    public List<StopTime> idsToEntities(List<Long> stopTimeIds) {
        return stopTimeIds.stream().map(this::idToEntity).collect(Collectors.toList());
    }

    public List<StopTimeDTO> idsToDTOs(List<Long> stopTimeIds) {
        return stopTimeIds.stream().map(this::idToEntity).collect(Collectors.toList()).stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    public List<StopTime> requestToEntities(List<StopTimeRequest> stopTimeRequests) {
        return stopTimeRequests.stream().map(this::requestToEntity).collect(Collectors.toList());
    }

    public List<StopTimeDTO> entitiesToDTOs(List<StopTime> stopTimes) {
        return stopTimes.stream().map(this::entityToDTO).collect(Collectors.toList());
    }

    public List<StopTimeDTO> requestToDTOs(List<StopTimeRequest> stopTimeRequests) {
        return stopTimeRequests.stream().map(this::requestToDTO).collect(Collectors.toList());
    }
}
