package com.hraczynski.trains.controllers;

import com.hraczynski.trains.exceptions.definitions.EntityNotFoundException;
import com.hraczynski.trains.train.*;
import com.hraczynski.trains.trip.TripService;
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
import java.util.Objects;
import java.util.Set;

import static com.hraczynski.trains.builders.TrainTestBuilder.*;
import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Train controller tests")
public class TrainControllerTest {

    @Mock
    private TrainRepresentationModelAssembler assembler;
    @Mock
    private TrainService trainService;
    @Mock
    private TripService tripService;
    @Captor
    private ArgumentCaptor<TrainRequest> argumentCaptor;

    private TrainController controller;

    @BeforeEach
    public void setup() {
        controller = new TrainController(trainService, tripService, assembler);
    }

    @Test
    @DisplayName("Find by id")
    void findById() {
        //given
        Train train = givenTrain();
        TrainDto trainDto = givenTrainDto();
        when(trainService.findById(anyLong())).thenReturn(train);
        when(assembler.toModel(train)).thenReturn(trainDto);

        //when
        ResponseEntity<TrainDto> responseEntity = controller.findById(train.getId());

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(trainDto);
        verify(trainService).findById(train.getId());
        verifyAssembler(train);
    }

    @Test
    @DisplayName("Doesn't find by id")
    void notFindById() {
        //given
        when(trainService.findById(anyLong())).thenThrow(new EntityNotFoundException(Train.class, "id = any"));

        //when
        //then
        assertThatCode(() -> controller.findById(anyLong())).isInstanceOf(EntityNotFoundException.class);
        verify(trainService).findById(anyLong());
        verifyNeverAssembler();
    }

    @Test
    @DisplayName("Find all")
    void findAll() {
        //given
        Set<Train> trainSet = givenTrainSet();
        CollectionModel<TrainDto> collectionModel = givenCollectionModel();
        when(trainService.findAll()).thenReturn(trainSet);
        when(assembler.toCollectionModel(trainSet)).thenReturn(collectionModel);

        //when
        ResponseEntity<CollectionModel<TrainDto>> all = controller.findAll();

        //then
        assertThat(all.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(all.getBody()).isEqualTo(collectionModel);
        verify(trainService).findAll();
        verifyAssembler(trainSet);

    }

    @Test
    @DisplayName("Doesn't find all")
    void notFindAll() {
        //given
        when(trainService.findAll()).thenThrow(new EntityNotFoundException(Train.class, "<none>"));

        //when
        //then
        assertThatCode(() -> controller.findAll()).isInstanceOf(EntityNotFoundException.class);
        verify(trainService).findAll();
        verifyNeverAssembler();
    }


    @Test
    @DisplayName("Delete by id")
    void deleteById() {
        //given
        Train train = givenTrain();
        TrainDto trainDto = givenTrainDto();
        when(trainService.deleteById(train.getId())).thenReturn(train);
        when(assembler.toModel(train)).thenReturn(trainDto);

        //when
        ResponseEntity<TrainDto> responseEntity = controller.deleteById(train.getId());

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isEqualTo(trainDto);
        verify(trainService).deleteById(train.getId());
        verifyAssembler(train);
    }

    @Test
    @DisplayName("Doesn't delete by id")
    void notDeleteById() {
        //given
        when(trainService.deleteById(anyLong())).thenThrow(EntityNotFoundException.class);

        //when
        //then
        assertThatCode(() -> controller.deleteById(anyLong())).isInstanceOf(EntityNotFoundException.class);
        verify(trainService).deleteById(anyLong());
        verifyNeverAssembler();
    }

    @Test
    @DisplayName("Updates train by train request")
    void update() {
        //given
        TrainRequest trainRequest = givenTrainRequest();

        //when
        ResponseEntity<Void> responseEntity = controller.update(trainRequest.getId(), trainRequest);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        verify(trainService).update(trainRequest.getId(), argumentCaptor.capture());
        assertThat(trainRequest).isEqualTo(argumentCaptor.getValue());

    }

    @Test
    @DisplayName("Patch by train request")
    void patchByTrainRequest() {
        //given
        TrainRequest trainRequest = givenTrainRequest();

        //when
        ResponseEntity<Void> responseEntity = controller.patch(trainRequest.getId(), trainRequest);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        verify(trainService).patch(trainRequest.getId(), argumentCaptor.capture());
        assertThat(trainRequest).isEqualTo(argumentCaptor.getValue());
    }

    @Test
    @DisplayName("Save train")
    void saveTrain() {
        //given
        TrainDto trainDto = givenTrainDto();
        Train train = givenTrain();
        TrainRequest trainRequest = givenTrainRequest();
        when(trainService.save(trainRequest)).thenReturn(train);
        when(assembler.toModel(train)).thenReturn(trainDto);

        //when
        ResponseEntity<TrainDto> responseEntity = controller.addTrain(trainRequest);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getBody()).isEqualTo(trainDto);

        verify(trainService).save(argumentCaptor.capture());
        assertThat(trainRequest).isEqualTo(argumentCaptor.getValue());
        verifyAssembler(train);
    }

    @Test
    @DisplayName("Doesn't save train")
    void notSave() {
        //given
        TrainRequest trainRequest = givenTrainRequest();
        when(trainService.save(trainRequest)).thenThrow(new RuntimeException());

        //when
        //then
        assertThatCode(() -> controller.addTrain(trainRequest)).isInstanceOf(RuntimeException.class);

        verify(trainService).save(argumentCaptor.capture());
        assertThat(trainRequest).isEqualTo(argumentCaptor.getValue());
        verifyNeverAssembler();
    }

    @Test
    @DisplayName("Get train image")
    void getTrainImage() {
        //given
        byte[] sample = new byte[]{10, 23, 56};
        when(trainService.getOneTrainImage(anyLong())).thenReturn(sample);

        //when
        ResponseEntity<byte[]> result = controller.getTrainImage(anyLong());

        //then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(sample);
        assertThat(Objects.requireNonNull(result.getHeaders().getContentType()).toString()).isEqualTo("images/svg+xml");
        verify(trainService).getOneTrainImage(anyLong());
    }

    @Test
    @DisplayName("Does not get train image")
    void notGetTrainImage() {
        //given
        when(trainService.getOneTrainImage(anyLong())).thenThrow(new RuntimeException());

        //when
        //then
        assertThatCode(() -> controller.getTrainImage(anyLong())).isInstanceOf(RuntimeException.class);
        verify(trainService).getOneTrainImage(anyLong());
    }


    private void verifyAssembler(Train train) {
        verify(assembler).toModel(train);
    }

    private void verifyAssembler(Set<Train> trains) {
        verify(assembler).toCollectionModel(trains);
    }

    private void verifyNeverAssembler() {
        verify(assembler, never()).toModel(any(Train.class));
    }

    private Set<Train> givenTrainSet() {
        return Set.of(givenTrain());
    }

    private CollectionModel<TrainDto> givenCollectionModel() {
        return CollectionModel.of(Collections.singleton(givenTrainDto()));
    }

    private Train givenTrain() {
        return make(a(BasicTrainEntity));
    }

    private TrainDto givenTrainDto() {
        return make(a(BasicTrainDto));
    }

    private TrainRequest givenTrainRequest() {
        return make(a(BasicTrainRequest));
    }


}
