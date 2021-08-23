package com.hraczynski.trains.stoptime;

import com.hraczynski.trains.city.*;
import com.hraczynski.trains.exceptions.definitions.EntityNotFoundException;
import com.hraczynski.trains.exceptions.definitions.IncoherentDataException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public record StopTimeMapper(
        CityRepresentationModelAssembler cityRepresentationModelAssembler, CityRepository cityRepository,
        ModelMapper mapper) {


    public StopTimeDTO entityToDTO(StopTime stopTime) {
        log.info("Mapping StopTime {} to StopTimeDTO", stopTime);
        StopTimeDTO stopTimeDTO = new StopTimeDTO();
        stopTimeDTO.setId(stopTime.getId());
        stopTimeDTO.setCityDTO(cityRepresentationModelAssembler.toModel(stopTime.getStop()));
        stopTimeDTO.setArrivalTime(stopTime.getArrivalTime());
        stopTimeDTO.setDepartureTime(stopTime.getDepartureTime());
        return stopTimeDTO;
    }

    public StopTime requestToEntity(StopTimeRequest stopTimeRequest) {
        log.info("Mapping StopTimeRequest {} to StopTime", stopTimeRequest);
        StopTime stopTime = new StopTime();
        City city = cityRepository.findById(stopTimeRequest.getCityRequest().getId())
                .orElseThrow(() -> {
                    log.error("Cannot find City with id = " + stopTimeRequest.getCityRequest().getId());
                    return new EntityNotFoundException(City.class, "id = " + stopTimeRequest.getCityRequest().getId());
                });
        validateCity(city, stopTimeRequest.getCityRequest());
        stopTime.setStop(city);
        stopTime.setId(stopTimeRequest.getId());
        stopTime.setArrivalTime(stopTimeRequest.getArrivalTime());
        stopTime.setDepartureTime(stopTimeRequest.getDepartureTime());
        return stopTime;
    }

    private void validateCity(City city, CityRequest cityRequest) {
        try {

            boolean isValid = city.getCountry().getName().equals(cityRequest.getCountry())
                    && city.getLat() == cityRequest.getLat()
                    && city.getLon() == cityRequest.getLon()
                    && city.getName().equals(cityRequest.getName());
            if (!isValid) {
                log.error("Provided data City and CityRequest with id = " + city.getId() + "are incoherent.");
                throw new IncoherentDataException(City.class, CityRequest.class, "id = " + city.getId());
            }
        } catch (Exception e) {
            log.error("Provided data City and CityRequest with id = " + city.getId() + "are incoherent.");
            throw new IncoherentDataException(City.class, CityRequest.class, "id = " + city.getId());
        }
    }

    public StopTimeDTO requestToDTO(StopTimeRequest stopTimeRequest) {
        log.info("Mapping StopTimeRequest {} to StopTimeDTO", stopTimeRequest);

        CityDTO cityDTO = mapper.map(stopTimeRequest.getCityRequest(), CityDTO.class);

        StopTimeDTO stopTimeDTO = new StopTimeDTO();
        stopTimeDTO.setId(stopTimeRequest.getId());
        stopTimeDTO.setCityDTO(cityDTO);
        stopTimeDTO.setArrivalTime(stopTimeRequest.getArrivalTime());
        stopTimeDTO.setDepartureTime(stopTimeRequest.getDepartureTime());
        return stopTimeDTO;
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
