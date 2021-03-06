package com.hraczynski.trains.assemblersmappers;

import com.hraczynski.trains.city.City;
import com.hraczynski.trains.city.CityDto;
import com.hraczynski.trains.city.CityRepository;
import com.hraczynski.trains.city.CityRepresentationModelAssembler;
import com.hraczynski.trains.stoptime.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.hraczynski.trains.builders.CityTestBuilder.BasicCityDto;
import static com.hraczynski.trains.builders.CityTestBuilder.BasicCityEntity;
import static com.hraczynski.trains.builders.StopTimeTestBuilder.*;
import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StoptimeMapper test")
public class StopTimeMapperTest {

    @Mock
    private CityRepository cityRepository;
    @Mock
    private StopsTimeRepository stopsTimeRepository;
    @Mock
    private CityRepresentationModelAssembler cityAssembler;
    private final ModelMapper modelMapper = new ModelMapper();
    private StopTimeMapper stopTimeMapper;

    @BeforeEach
    void setup() {
        stopTimeMapper = new StopTimeMapper(cityAssembler, cityRepository, stopsTimeRepository, modelMapper);
    }

    @Test
    @DisplayName("Entity to dto test")
    void entityToDtoTest() {
        //given
        StopTime stopTime = givenStopTime();
        CityDto cityDto = givenCityDto();
        when(cityAssembler.toModel(any(City.class))).thenReturn(cityDto);

        //when
        StopTimeDto stopTimeDto = stopTimeMapper.entityToDto(stopTime);

        //then
        assertThat(stopTimeDto.getId()).isEqualTo(stopTime.getId());
        assertThat(stopTimeDto.getDepartureTime()).isEqualTo(stopTime.getDepartureTime());
        assertThat(stopTimeDto.getArrivalTime()).isEqualTo(stopTime.getArrivalTime());
        assertThat(stopTimeDto.getCityDto().getId()).isEqualTo(stopTime.getStop().getId());
        assertThat(stopTimeDto.getCityDto().getLat()).isEqualTo(stopTime.getStop().getLat());
        assertThat(stopTimeDto.getCityDto().getLon()).isEqualTo(stopTime.getStop().getLon());
        assertThat(stopTimeDto.getCityDto().getCountry()).isEqualTo(stopTime.getStop().getCountry().getName());
        verify(cityAssembler).toModel(any(City.class));
    }

    @Test
    @DisplayName("Request to entity test")
    void requestToEntityTest() {
        //given
        StopTimeRequest stopTimeRequest = givenStopTimeRequest();
        City city = givenCity();
        when(cityRepository.findById(anyLong())).thenReturn(Optional.ofNullable(city));

        //when
        StopTime stopTime = stopTimeMapper.requestToEntity(stopTimeRequest);

        //then
        assertThat(stopTime.getId()).isEqualTo(stopTimeRequest.getId());
        assertThat(stopTime.getDepartureTime()).isEqualTo(stopTimeRequest.getDepartureTime());
        assertThat(stopTime.getArrivalTime()).isEqualTo(stopTimeRequest.getArrivalTime());
        assertThat(stopTime.getStop().getId()).isEqualTo(stopTimeRequest.getCityId());
        verify(cityRepository).findById(anyLong());
    }

    @Test
    @DisplayName("Request to dto test")
    void requestToDtoTest() {
        //given
        StopTimeRequest stopTimeRequest = givenStopTimeRequest();
        City city = givenCity();
        when(cityRepository.findById(anyLong())).thenReturn(Optional.ofNullable(city));

        //when
        StopTimeDto stopTimeDto = stopTimeMapper.requestToDto(stopTimeRequest);

        //then
        assertThat(stopTimeDto.getId()).isEqualTo(stopTimeRequest.getId());
        assertThat(stopTimeDto.getDepartureTime()).isEqualTo(stopTimeRequest.getDepartureTime());
        assertThat(stopTimeDto.getArrivalTime()).isEqualTo(stopTimeRequest.getArrivalTime());
        assertThat(stopTimeDto.getCityDto().getId()).isEqualTo(stopTimeRequest.getCityId());
        verify(cityRepository).findById(anyLong());
    }

    @Test
    @DisplayName("Id to entity test")
    void idToEntityTest() {
        //given
        StopTime stopTime = givenStopTime();
        Long id = stopTime.getId();
        when(stopsTimeRepository.findById(id)).thenReturn(Optional.of(stopTime));

        //when
        StopTime result = stopTimeMapper.idToEntity(id);

        //then
        assertThat(result).usingRecursiveComparison().isEqualTo(stopTime);
    }

    @Test
    @DisplayName("Id to entity test (many)")
    void idToEntityManyTest() {
        //given
        StopTime stopTime = givenStopTime();
        StopTime stopTime2 = givenStopTime();
        List<Long> idList = List.of(stopTime.getId(), stopTime2.getId());
        doReturn(Optional.of(stopTime), Optional.of(stopTime2)).when(stopsTimeRepository).findById(idList.get(0));

        //when
        List<StopTime> results = stopTimeMapper.idsToEntities(idList);

        //then
        assertThat(results.size()).isEqualTo(idList.size());
        assertThat(results.stream().map(StopTime::getId).collect(Collectors.toList())).isEqualTo(idList);
    }

    @Test
    @DisplayName("Id to dtos test (many)")
    void idsToDtoManyTest() {
        //given
        StopTime stopTime = givenStopTime();
        StopTime stopTime2 = givenStopTime();
        CityDto cityDto = givenCityDto();
        CityDto cityDto2 = givenCityDto();
        List<Long> idList = List.of(stopTime.getId(), stopTime2.getId());
        doReturn(Optional.of(stopTime), Optional.of(stopTime2)).when(stopsTimeRepository).findById(idList.get(0));
        when(cityAssembler.toModel(any(City.class))).thenReturn(cityDto, cityDto2);

        //when
        List<StopTimeDto> results = stopTimeMapper.idsToDtos(idList);

        //then
        assertThat(results.size()).isEqualTo(idList.size());
        assertThat(results.stream().map(StopTimeDto::getId).collect(Collectors.toList())).isEqualTo(idList);
        verify(cityAssembler, times(idList.size())).toModel(any(City.class));
    }

    @Test
    @DisplayName("Request to entity test (many)")
    void requestToEntityManyTest() {
        //given
        StopTimeRequest stopTimeRequest = givenStopTimeRequest();
        StopTimeRequest stopTimeRequest2 = givenStopTimeRequest();
        City city = givenCity();
        City city2 = givenCity();
        List<StopTimeRequest> stopTimeRequestList = List.of(stopTimeRequest, stopTimeRequest2);
        doReturn(Optional.ofNullable(city), Optional.ofNullable(city2)).when(cityRepository).findById(anyLong());

        //when
        List<StopTime> results = stopTimeMapper.requestToEntities(stopTimeRequestList);

        //then
        assertThat(results.size()).isEqualTo(stopTimeRequestList.size());
        assertThat(results.stream().map(StopTime::getId).collect(Collectors.toList())).isEqualTo(stopTimeRequestList.stream().map(StopTimeRequest::getId).collect(Collectors.toList()));
    }

    @Test
    @DisplayName("Entities to Dtos test (many)")
    void entitiesToDtosManyTest() {
        //given
        StopTime stopTime = givenStopTime();
        StopTime stopTime2 = givenStopTime();
        List<StopTime> listOfEntities = List.of(stopTime, stopTime2);
        CityDto cityDto = givenCityDto();
        CityDto cityDto2 = givenCityDto();
        when(cityAssembler.toModel(any(City.class))).thenReturn(cityDto, cityDto2);

        //when
        List<StopTimeDto> result = stopTimeMapper.entitiesToDtos(listOfEntities);

        //then
        assertThat(result.size()).isEqualTo(listOfEntities.size());
        assertThat(result.stream().map(StopTimeDto::getId).collect(Collectors.toList())).isEqualTo(listOfEntities.stream().map(StopTime::getId).collect(Collectors.toList()));
    }

    @Test
    @DisplayName("Request to Dtos test (many)")
    void requestToDtosTestMany() {
        //given
        StopTimeRequest stopTimeRequest = givenStopTimeRequest();
        StopTimeRequest stopTimeRequest2 = givenStopTimeRequest();
        List<StopTimeRequest> listOfRequests = List.of(stopTimeRequest, stopTimeRequest2);
        City city = givenCity();
        City city2 = givenCity();
        doReturn(Optional.of(city), Optional.of(city2)).when(cityRepository).findById(anyLong());

        //when
        List<StopTime> stopTimes = stopTimeMapper.requestToEntities(listOfRequests);

        //then
        assertThat(stopTimes.size()).isEqualTo(listOfRequests.size());
        assertThat(stopTimes.stream().map(StopTime::getId).collect(Collectors.toList())).isEqualTo(listOfRequests.stream().map(StopTimeRequest::getId).collect(Collectors.toList()));

    }

    private StopTimeDto givenStopTimeDto() {
        return make(a(BasicStopTimeDto));
    }

    private StopTimeRequest givenStopTimeRequest() {
        return make(a(BasicStopTimeRequest));
    }

    private CityDto givenCityDto() {
        return make(a(BasicCityDto));
    }

    private City givenCity() {
        return make(a(BasicCityEntity));
    }

    private StopTime givenStopTime() {
        return make(a(BasicStopTimeEntity));
    }
}
