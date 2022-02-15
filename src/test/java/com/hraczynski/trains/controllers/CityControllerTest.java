package com.hraczynski.trains.controllers;

import com.hraczynski.trains.city.*;
import com.hraczynski.trains.exceptions.definitions.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.hraczynski.trains.builders.CityTestBuilder.*;
import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("City controller tests")
class CityControllerTest {

    private static final Long ID = 10L;
    @Mock
    private CityRepresentationModelAssembler assembler;
    @Mock
    private CityService cityService;
    @Captor
    private ArgumentCaptor<CityRequest> argumentCaptor;

    private CityController controller;

    @BeforeEach
    public void setup() {
        controller = new CityController(cityService, assembler);
    }

    @Test
    @DisplayName("Find by id")
    void findById() {
        //given
        City city = givenCity();
        CityDto cityDto = givenDto();
        when(cityService.findById(anyLong())).thenReturn(city);
        when(assembler.toModel(city)).thenReturn(cityDto);

        //when
        ResponseEntity<CityDto> responseEntity = controller.findById(city.getId());

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(cityDto);
        verify(cityService).findById(city.getId());
        verifyAssembler(city);
    }

    @Test
    @DisplayName("Doesn't find by id")
    void notFindById() {
        //given
        when(cityService.findById(ID)).thenThrow(new EntityNotFoundException(City.class, "id = " + ID));

        //when
        //then
        assertThatCode(() -> controller.findById(ID)).isInstanceOf(EntityNotFoundException.class);
        verify(cityService).findById(ID);
        verifyNeverAssembler();
    }

    @Test
    @DisplayName("Find all")
    void findAll() {
        //given
        Set<City> citySet = givenCitySet();
        CollectionModel<CityDto> collectionModel = givenCollectionModel();
        when(cityService.findAll()).thenReturn(citySet);
        when(assembler.toCollectionModel(citySet)).thenReturn(collectionModel);

        //when
        ResponseEntity<CollectionModel<CityDto>> all = controller.findAll();

        //then
        assertThat(all.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(all.getBody()).isEqualTo(collectionModel);
        verify(cityService).findAll();
        verifyAssembler(citySet);

    }

    @Test
    @DisplayName("Doesn't find all")
    void notFindAll() {
        //given
        when(cityService.findAll()).thenThrow(new EntityNotFoundException(City.class, "<none>"));

        //when
        //then
        assertThatCode(() -> controller.findAll()).isInstanceOf(EntityNotFoundException.class);
        verify(cityService).findAll();
        verifyNeverAssembler();
    }

    @Test
    @DisplayName("Delete by id")
    void deleteById() {
        //given
        City city = givenCity();
        CityDto cityDto = givenDto();
        when(cityService.deleteById(city.getId())).thenReturn(city);
        when(assembler.toModel(city)).thenReturn(cityDto);

        //when
        ResponseEntity<CityDto> responseEntity = controller.deleteById(city.getId());

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(cityDto);
        verify(cityService).deleteById(city.getId());
        verifyAssembler(city);
    }

    @Test
    @DisplayName("Doesn't delete by id")
    void notDeleteById() {
        //given
        when(cityService.deleteById(ID)).thenThrow(EntityNotFoundException.class);

        //when
        //then
        assertThatCode(() -> controller.deleteById(ID)).isInstanceOf(EntityNotFoundException.class);
        verify(cityService).deleteById(ID);
        verifyNeverAssembler();
    }

    @Test
    @DisplayName("Updates city by city request")
    void update() {
        //given
        CityRequest cityRequest = givenRequest();

        //when
        ResponseEntity<Void> responseEntity = controller.update(cityRequest);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        verify(cityService).update(argumentCaptor.capture());
        assertThat(cityRequest).isEqualTo(argumentCaptor.getValue());

    }

    @Test
    @DisplayName("Patch by city request")
    void patchByCityRequest() {
        //given
        CityRequest city = givenRequest();

        //when
        ResponseEntity<Void> responseEntity = controller.patchById(city);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        verify(cityService).patch(argumentCaptor.capture());
        assertThat(city).isEqualTo(argumentCaptor.getValue());
    }

    @Test
    @DisplayName("Save city")
    void saveCity() {
        //given
        CityDto cityDto = givenDto();
        City city = givenCity();
        CityRequest cityRequest = givenRequest();
        when(cityService.save(cityRequest)).thenReturn(city);
        when(assembler.toModel(city)).thenReturn(cityDto);

        //when
        ResponseEntity<CityDto> responseEntity = controller.addCity(cityRequest);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getBody()).isEqualTo(cityDto);

        verify(cityService).save(argumentCaptor.capture());
        assertThat(cityRequest).isEqualTo(argumentCaptor.getValue());
        verifyAssembler(city);
    }

    @Test
    @DisplayName("Doesn't save city")
    void notSaveCity() {
        //given
        CityRequest city = givenRequest();
        when(cityService.save(city)).thenThrow(new RuntimeException());

        //when
        //then
        assertThatCode(() -> controller.addCity(city)).isInstanceOf(RuntimeException.class);

        verify(cityService).save(argumentCaptor.capture());
        assertThat(city).isEqualTo(argumentCaptor.getValue());
        verifyNeverAssembler();
    }

    private void verifyNeverAssembler() {
        verify(assembler,never()).toModel(any(City.class));
    }

    private void verifyAssembler(City city) {
        verify(assembler).toModel(city);
    }

    private void verifyAssembler(Set<City> cities) {
        verify(assembler).toCollectionModel(cities);
    }

    private CollectionModel<CityDto> givenCollectionModel() {
        return CollectionModel.of(Collections.singletonList(givenDto()));
    }

    private CityDto givenDto() {
        return make(a(BasicCityDto));
    }

    private CityRequest givenRequest() {
        return make(a(BasicCityRequest));
    }

    private City givenCity() {
        return make(a(BasicCityEntity));
    }

    private Set<City> givenCitySet() {
        return new HashSet<>(List.of(givenCity()));
    }

}