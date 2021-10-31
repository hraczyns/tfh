package com.hraczynski.trains.controllers;

import com.hraczynski.trains.exceptions.definitions.EntityNotFoundException;
import com.hraczynski.trains.passengers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import java.util.Set;

import static com.hraczynski.trains.builders.PassengerTestBuilder.*;
import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Passenger controller tests")
class PassengerControllerTest {

    private static final Long ID = 10L;

    @Mock
    private PassengerService passengerService;
    @Mock
    private PassengerRepresentationModelAssembler assembler;
    @Captor
    private ArgumentCaptor<PassengerRequest> argumentCaptor;

    private PassengerController controller;


    @BeforeEach
    public void setup() {
        controller = new PassengerController(passengerService, assembler);
    }

    @Test
    @DisplayName("Find by id")
    void findById() {
        //given
        Passenger passenger = givenEntity();
        PassengerDTO passengerDTO = givenDTO();
        when(passengerService.getById(anyLong())).thenReturn(passenger);
        when(assembler.toModel(passenger)).thenReturn(passengerDTO);

        //when
        ResponseEntity<PassengerDTO> responseEntity = controller.getById(passenger.getId());

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(passengerDTO);
        verify(passengerService).getById(passenger.getId());
        verifyAssembler(passenger);
    }


    @Test
    @DisplayName("Find all")
    void findAll() {
        //given
        Set<Passenger> passengers = preparedSet();
        CollectionModel<PassengerDTO> collectionModel = preparedCollectionModel();
        when(passengerService.getAll()).thenReturn(passengers);
        when(assembler.toCollectionModel(passengers)).thenReturn(collectionModel);

        //when
        ResponseEntity<CollectionModel<PassengerDTO>> all = controller.getAll();

        //then
        assertThat(all.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(all.getBody()).isEqualTo(collectionModel);
        verifyAssembler(passengers);

    }


    @Test
    @DisplayName("Delete by id")
    void deleteById() {
        //given
        Passenger passenger = givenEntity();
        PassengerDTO passengerDTO = givenDTO();
        when(passengerService.deleteById(passengerDTO.getId())).thenReturn(passenger);
        when(assembler.toModel(passenger)).thenReturn(passengerDTO);

        //when
        ResponseEntity<PassengerDTO> responseEntity = controller.deleteById(passengerDTO.getId());

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(passengerDTO);
        verify(passengerService).deleteById(passengerDTO.getId());
        verifyAssembler(passenger);
    }

    @Test
    @DisplayName("Updates passenger by passenger request")
    void update() {
        //given
        PassengerRequest passengerRequest = givenRequest();

        //when
        ResponseEntity<Void> responseEntity = controller.update(passengerRequest);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        verify(passengerService).update(argumentCaptor.capture());
        assertThat(passengerRequest).isEqualTo(argumentCaptor.getValue());
    }

    @Test
    @DisplayName("Patch by passenger request")
    void patchByPassengerRequest() {
        //given
        PassengerRequest passengerRequest = givenRequest();

        //when
        ResponseEntity<Void> responseEntity = controller.patch(passengerRequest);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        verify(passengerService).patch(argumentCaptor.capture());
        assertThat(passengerRequest).isEqualTo(argumentCaptor.getValue());
    }

    @Test
    @DisplayName("Save passenger")
    void savePassenger() {
        //given
        Passenger passenger = givenEntity();
        PassengerDTO passengerDTO = givenDTO();
        PassengerRequest passengerRequest = givenRequest();
        when(passengerService.addPassenger(passengerRequest)).thenReturn(passenger);
        when(assembler.toModel(passenger)).thenReturn(passengerDTO);

        //when
        ResponseEntity<PassengerDTO> responseEntity = controller.addPassenger(passengerRequest);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getBody()).isEqualTo(passengerDTO);

        verify(passengerService).addPassenger(argumentCaptor.capture());
        assertThat(passengerRequest).isEqualTo(argumentCaptor.getValue());
        verifyAssembler(passenger);
    }

    @Nested
    @DisplayName("Negative cases ended up with exception")
    class NegativeCases {

        @Test
        @DisplayName("Doesn't find by id")
        void notFindById() {
            //given
            when(passengerService.getById(ID)).thenThrow(new EntityNotFoundException(Passenger.class, "id = " + ID));

            //when
            //then
            assertThatCode(() -> controller.getById(ID)).isInstanceOf(EntityNotFoundException.class);
            verify(passengerService).getById(ID);
            verifyNeverAssembler();
        }

        @Test
        @DisplayName("Doesn't find all")
        void notFindAll() {
            //given
            when(passengerService.getAll()).thenThrow(new EntityNotFoundException(Passenger.class, "<none>"));

            //when
            //then
            assertThatCode(() -> controller.getAll()).isInstanceOf(EntityNotFoundException.class);
            verify(passengerService).getAll();
            verifyNeverAssembler();
        }

        @Test
        @DisplayName("Doesn't delete by id")
        void notDeleteById() {
            //given
            when(passengerService.deleteById(ID)).thenThrow(EntityNotFoundException.class);

            //when
            //then
            assertThatCode(() -> controller.deleteById(ID)).isInstanceOf(EntityNotFoundException.class);
            verify(passengerService).deleteById(ID);
            verifyNeverAssembler();
        }

        @Test
        @DisplayName("Doesn't save passenger")
        void notSaveCity() {
            //given
            PassengerRequest passengerRequest = givenRequest();
            when(passengerService.addPassenger(passengerRequest)).thenThrow(new RuntimeException());

            //when
            //then
            assertThatCode(() -> controller.addPassenger(passengerRequest)).isInstanceOf(RuntimeException.class);

            verify(passengerService).addPassenger(argumentCaptor.capture());
            assertThat(passengerRequest).isEqualTo(argumentCaptor.getValue());
            verifyNeverAssembler();
        }
    }

    private CollectionModel<PassengerDTO> preparedCollectionModel() {
        return CollectionModel.of(Collections.singletonList(givenDTO()));
    }

    private PassengerDTO givenDTO() {
        return make(a(BasicPassengerDTO));
    }

    private PassengerRequest givenRequest() {
        return make(a(BasicPassengerRequest));
    }

    private Passenger givenEntity() {
        return make(a(BasicPassengerEntity));
    }

    private Set<Passenger> preparedSet() {
        return Set.of(givenEntity());
    }

    private void verifyAssembler(Passenger passenger) {
        verify(assembler).toModel(passenger);
    }

    private void verifyAssembler(Set<Passenger> passengers) {
        verify(assembler).toCollectionModel(passengers);
    }

    private void verifyNeverAssembler() {
        verify(assembler, never()).toModel(any(Passenger.class));
    }


}