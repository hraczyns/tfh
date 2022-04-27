package com.hraczynski.trains.stoptime;

import com.hraczynski.trains.city.City;
import com.hraczynski.trains.city.CityDto;
import com.hraczynski.trains.city.CityRepository;
import com.hraczynski.trains.city.CityRepresentationModelAssembler;
import com.hraczynski.trains.exceptions.definitions.EntityNotFoundException;
import com.hraczynski.trains.trip.Trip;
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


    public StopTimeDto entityToDto(StopTime stopTime) {
        StopTimeDto stopTimeDto = new StopTimeDto();
        stopTimeDto.setId(stopTime.getId());
        stopTimeDto.setCityDto(cityRepresentationModelAssembler.toModel(stopTime.getStop()));
        stopTimeDto.setArrivalTime(stopTime.getArrivalTime());
        stopTimeDto.setDepartureTime(stopTime.getDepartureTime());
        return stopTimeDto;
    }

    public StopTimeExtendedDto entityToExtendedDto(StopTime stopTime) {
        StopTimeDto stopTimeDto = entityToDto(stopTime);
        Trip trip = stopTime.getTrip();
        StopTimeExtendedDto stopTimeExtendedDto = new StopTimeExtendedDto();
        stopTimeExtendedDto.setStopTimeDto(stopTimeDto);
        stopTimeExtendedDto.setTripId(trip.getId());
        stopTimeExtendedDto.setTrainId(trip.getTrain().getId());
        stopTimeExtendedDto.setTrainUnique(trip.getTrain().getRepresentationUnique());
        stopTimeExtendedDto.setTrainClass(trip.getTrain().getModel().name());

        StopTimeDto nextStopTimeDto = null;
        List<StopTime> stopTimes = trip.getStopTimes();
        for (int j = 0; j < stopTimes.size(); j++) {
            StopTime stopTimeTemp = stopTimes.get(j);
            if (stopTimeTemp.equals(stopTime) && j != stopTimes.size() - 1) {
                nextStopTimeDto = entityToDto(stopTimes.get(j + 1));
            }
        }
        stopTimeExtendedDto.setNextStopTimeDto(nextStopTimeDto);
        return stopTimeExtendedDto;
    }

    public StopTime requestToEntity(StopTimeRequest stopTimeRequest) {
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

    public StopTimeDto requestToDto(StopTimeRequest stopTimeRequest) {
        City city = findCityById(stopTimeRequest.getCityId());
        CityDto cityDto = mapper.map(city, CityDto.class);

        StopTimeDto stopTimeDto = new StopTimeDto();
        stopTimeDto.setId(stopTimeRequest.getId());
        stopTimeDto.setCityDto(cityDto);
        stopTimeDto.setArrivalTime(stopTimeRequest.getArrivalTime());
        stopTimeDto.setDepartureTime(stopTimeRequest.getDepartureTime());
        return stopTimeDto;
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

    public List<StopTimeDto> idsToDtos(List<Long> stopTimeIds) {
        return stopTimeIds.stream().map(this::idToEntity).collect(Collectors.toList()).stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    public List<StopTime> requestToEntities(List<StopTimeRequest> stopTimeRequests) {
        return stopTimeRequests.stream().map(this::requestToEntity).collect(Collectors.toList());
    }

    public List<StopTimeDto> entitiesToDtos(List<StopTime> stopTimes) {
        return stopTimes.stream().map(this::entityToDto).collect(Collectors.toList());
    }

    public List<StopTimeDto> requestToDtos(List<StopTimeRequest> stopTimeRequests) {
        return stopTimeRequests.stream().map(this::requestToDto).collect(Collectors.toList());
    }
}
