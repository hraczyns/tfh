package com.hraczynski.trains.controllers.unit;

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

import static com.hraczynski.trains.controllers.CityTestBuilder.BasicCityDTO;
import static com.hraczynski.trains.controllers.CityTestBuilder.BasicCityRequest;
import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("City controller tests")
class CityControllerTest {

    private static final Long ID = 10L;

    @Mock
    private CityService cityService;
    @Captor
    private ArgumentCaptor<CityRequest> argumentCaptor;

    private CityController controller;

    @BeforeEach
    public void setup() {
        controller = new CityController(cityService);
    }

    @Test
    @DisplayName("Find by id")
    void findById() {
        //given
        CityDTO cityDTO = givenDTO();
        when(cityService.getById(anyLong())).thenReturn(cityDTO);

        //when
        ResponseEntity<CityDTO> responseEntity = controller.findById(cityDTO.getId());

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(cityDTO);
        verify(cityService).getById(cityDTO.getId());
    }

    @Test
    @DisplayName("Doesn't find by id")
    void notFindById() {
        //given
        when(cityService.getById(ID)).thenThrow(new EntityNotFoundException(City.class, "id = " + ID));

        //when
        //then
        assertThatCode(()->controller.findById(ID)).isInstanceOf(EntityNotFoundException.class);
        verify(cityService).getById(ID);
    }

    @Test
    @DisplayName("Find all")
    void findAll() {
        //given
        CollectionModel<CityDTO> collectionModel = preparedCollectionModel();
        when(cityService.findAll()).thenReturn(collectionModel);

        //when
        ResponseEntity<CollectionModel<CityDTO>> all = controller.findAll();

        //then
        assertThat(all.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(all.getBody()).isEqualTo(collectionModel);

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
    }

    @Test
    @DisplayName("Delete by id")
    void deleteById() {
        //given
        CityDTO cityDTO = givenDTO();
        when(cityService.deleteById(cityDTO.getId())).thenReturn(cityDTO);

        //when
        ResponseEntity<CityDTO> responseEntity = controller.deleteById(cityDTO.getId());

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(cityDTO);
        verify(cityService).deleteById(cityDTO.getId());
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
    }

    @Test
    @DisplayName("Updates city by city request")
    void update() {
        //given
        CityDTO cityDTO = givenDTO();
        CityRequest cityRequest = givenRequest();
        when(cityService.update(cityRequest)).thenReturn(cityDTO);

        //when
        ResponseEntity<Void> responseEntity = controller.update(cityRequest);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        verify(cityService).update(argumentCaptor.capture());
        assertThat(cityRequest).isEqualTo(argumentCaptor.getValue());
    }

    @Test
    @DisplayName("Doesn't update city by city request")
    void notUpdate() {
        //given
        CityRequest city = givenRequest();
        when(cityService.update(city)).thenThrow(new EntityNotFoundException(City.class, "id = " + ID));

        //when
        //then
        assertThatCode(() -> controller.update(city)).isInstanceOf(EntityNotFoundException.class);

        verify(cityService).update(argumentCaptor.capture());
        assertThat(city).isEqualTo(argumentCaptor.getValue());
    }

    @Test
    @DisplayName("Patch by city request")
    void patchByCityRequest() {
        //given
        CityDTO cityDTO = givenDTO();
        CityRequest city = givenRequest();
        when(cityService.patchById(city)).thenReturn(cityDTO);

        //when
        ResponseEntity<Void> responseEntity = controller.patchById(city);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        verify(cityService).patchById(argumentCaptor.capture());
        assertThat(city).isEqualTo(argumentCaptor.getValue());
    }

    @Test
    @DisplayName("Doesn't patch city by city request")
    void notPatchByCityRequest() {
        //given
        CityRequest city = givenRequest();
        when(cityService.patchById(city)).thenThrow(new EntityNotFoundException(City.class, "id = " + ID));

        //when
        //then
        assertThatCode(() -> controller.patchById(city)).isInstanceOf(EntityNotFoundException.class);

        verify(cityService).patchById(argumentCaptor.capture());
        assertThat(city).isEqualTo(argumentCaptor.getValue());
    }

    @Test
    @DisplayName("Save city")
    void saveCity() {
        //given
        CityDTO cityDTO = givenDTO();
        CityRequest city = givenRequest();
        when(cityService.save(city)).thenReturn(cityDTO);

        //when
        ResponseEntity<CityDTO> responseEntity = controller.addCity(city);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getBody()).isEqualTo(cityDTO);

        verify(cityService).save(argumentCaptor.capture());
        assertThat(city).isEqualTo(argumentCaptor.getValue());
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
    }

    private CollectionModel<CityDTO> preparedCollectionModel() {
        return CollectionModel.of(Collections.singletonList(givenDTO()));
    }

    private CityDTO givenDTO() {
        return make(a(BasicCityDTO));
    }

    private CityRequest givenRequest() {
        return make(a(BasicCityRequest));
    }
}