package com.hraczynski.trains.controllers;

import com.hraczynski.trains.exceptions.definitions.EntityNotFoundException;
import com.hraczynski.trains.trip.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Set;

import static com.hraczynski.trains.builders.TripTestBuilder.BasicTripDto;
import static com.hraczynski.trains.builders.TripTestBuilder.BasicTripEntity;
import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Trip controller tests")
public class TripControllerTest {

    private static final Long ID = 1L;
    private static final Long TRAIN_ID = 10L;
    @Mock
    private TripRepresentationModelAssembler assembler;
    @Mock
    private TripService tripService;

    private TripController controller;

    @BeforeEach
    public void setup() {
        controller = new TripController(tripService, assembler);
    }

    @DisplayName("Get by id")
    @Test
    void getById() {
        //given
        TripDto tripDto = givenTripDto();
        Trip trip = givenTrip();
        when(tripService.getById(trip.getId())).thenReturn(trip);
        when(assembler.toModel(trip)).thenReturn(tripDto);

        //when
        ResponseEntity<TripDto> byId = controller.getById(trip.getId());

        //then
        assertThat(byId.getBody()).isEqualTo(tripDto);
        assertThat(byId.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(tripService).getById(trip.getId());
        verifyAssembler(trip);
    }

    @DisplayName("Does not get by id")
    @Test
    void notGetById() {
        //given
        when(tripService.getById(ID)).thenThrow(new EntityNotFoundException(Trip.class, "id = " + ID));

        //when
        //then
        assertThatCode(() -> controller.getById(ID)).isInstanceOf(EntityNotFoundException.class);
        verify(tripService).getById(ID);
        verifyNeverAssembler();
    }

    @DisplayName("Get all")
    @Test
    void getAll() {
        //given
        Set<Trip> tripSet = givenSet();
        CollectionModel<TripDto> tripDtos = givenCollectionModel();
        when(tripService.getAll()).thenReturn(tripSet);
        when(assembler.toCollectionModel(tripSet)).thenReturn(tripDtos);

        //when
        ResponseEntity<CollectionModel<TripDto>> all = controller.getAll();

        //then
        assertThat(all.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(all.getBody()).isEqualTo(tripDtos);
        verify(tripService).getAll();
        verifyAssembler(tripSet);
    }

    @DisplayName("Does not get all")
    @Test
    void notGetAll() {
        //given
        when(tripService.getAll()).thenThrow(new EntityNotFoundException(Trip.class, "none"));

        //when
        //then
        assertThatCode(() -> controller.getAll()).isInstanceOf(EntityNotFoundException.class);
        verify(tripService).getAll();
        verifyNeverAssembler();
    }

    @DisplayName("Get all by trainId")
    @Test
    void getAllByTrainId() {
        //given
        Set<Trip> tripSet = givenSet();
        CollectionModel<TripDto> tripDtos = givenCollectionModel();
        when(tripService.getTripsByTrainId(TRAIN_ID)).thenReturn(tripSet);
        when(assembler.toCollectionModel(tripSet)).thenReturn(tripDtos);

        //when
        ResponseEntity<CollectionModel<TripDto>> all = controller.getTripsByTrainId(TRAIN_ID);

        //then
        assertThat(all.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(all.getBody()).isEqualTo(tripDtos);
        verify(tripService).getTripsByTrainId(TRAIN_ID);
        verifyAssembler(tripSet);
    }

    @DisplayName("Does not get all by train id")
    @Test
    void notGetAllByTrainId() {
        //given
        when(tripService.getTripsByTrainId(TRAIN_ID)).thenThrow(new EntityNotFoundException(Trip.class, "trainId = " + TRAIN_ID));

        //when
        //then
        assertThatCode(() -> controller.getTripsByTrainId(TRAIN_ID)).isInstanceOf(EntityNotFoundException.class);
        verify(tripService).getTripsByTrainId(TRAIN_ID);
        verifyNeverAssembler();
    }

    private void verifyAssembler(Set<Trip> tripSet) {
        verify(assembler).toCollectionModel(tripSet);
    }

    private void verifyAssembler(Trip trip) {
        verify(assembler).toModel(trip);
    }

    private void verifyNeverAssembler() {
        verify(assembler, never()).toModel(any(Trip.class));
    }

    private CollectionModel<TripDto> givenCollectionModel() {
        return CollectionModel.of(Collections.singleton(givenTripDto()));
    }

    private Set<Trip> givenSet() {
        return Collections.singleton(givenTrip());
    }

    private TripDto givenTripDto() {
        return make(a(BasicTripDto));
    }

    private Trip givenTrip() {
        return make(a(BasicTripEntity));
    }
}
