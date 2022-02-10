package com.hraczynski.trains.services;

import com.hraczynski.trains.exceptions.definitions.EntityNotFoundException;
import com.hraczynski.trains.images.ImagesProcessor;
import com.hraczynski.trains.train.Train;
import com.hraczynski.trains.train.TrainRepository;
import com.hraczynski.trains.train.TrainService;
import com.hraczynski.trains.train.TrainServiceImpl;
import com.hraczynski.trains.utils.BeanUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationContext;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.hraczynski.trains.builders.TrainTestBuilder.BasicTrainEntity;
import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Train service tests")
public class TrainServiceTest {

    @Mock
    private TrainRepository trainRepository;
    @Mock
    private ApplicationContext applicationContext;
    @Mock
    private ImagesProcessor processor;
    @Captor
    private ArgumentCaptor<Train> argumentCaptor;
    private TrainService service;
    private final ModelMapper modelMapper = new ModelMapper();


    @BeforeEach
    public void setup() {
        service = new TrainServiceImpl(modelMapper, trainRepository, processor);
    }

    @Test
    @DisplayName("Delete city by id")
    void deleteById() {
        //given
        Train train = givenTrain();
        when(trainRepository.findById(train.getId())).thenReturn(Optional.of(train));
        whenBeanUtil();

        //when
        Train result = service.deleteById(train.getId());

        //then
        assertThat(result).isEqualTo(train);
        verify(trainRepository).findById(train.getId());
        verify(trainRepository).deleteById(train.getId());
    }

    @Test
    @DisplayName("Does not delete train by id")
    void notDeleteById() {
        //given
        Train train = givenTrain();
        when(trainRepository.findById(anyLong())).thenReturn(Optional.empty());
        whenBeanUtil();

        //when
        //then
        assertThatCode(() -> service.deleteById(train.getId())).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("Find by id")
    void findById() {
        //given
        Train train = givenTrain();
        when(trainRepository.findById(train.getId())).thenReturn(Optional.of(train));
        whenBeanUtil();

        //when
        Train result = service.findById(train.getId());

        //then
        assertThat(result).isEqualTo(train);
        verify(trainRepository).findById(train.getId());
    }

    @DisplayName("Doesn't get by id")
    @Test
    void notGetById() {
        //given
        when(trainRepository.findById(anyLong())).thenReturn(Optional.empty());
        whenBeanUtil();

        //when
        //then
        assertThatCode(() -> service.findById(anyLong())).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("Find all")
    void findAll() {
        //given
        Set<Train> trainSet = givenTrainSet();
        when(trainRepository.findAll()).thenReturn(trainSet);

        //when
        Set<Train> result = service.findAll();

        //then
        assertThat(result).isEqualTo(trainSet);
        verify(trainRepository).findAll();
    }

    @DisplayName("Doesn't get all")
    @Test
    void notGetAll() {
        //given
        when(trainRepository.findAll()).thenReturn(new HashSet<>());

        //when
        //then
        assertThatCode(() -> service.findAll()).isInstanceOf(EntityNotFoundException.class);
        verify(trainRepository).findAll();
    }

    private Set<Train> givenTrainSet() {
        return new HashSet<>(List.of(givenTrain()));
    }

    private Train givenTrain() {
        return make(a(BasicTrainEntity));
    }

    @SuppressWarnings("all")
    private void whenBeanUtil() {
        BeanUtil beanUtil = new BeanUtil();
        beanUtil.setApplicationContext(applicationContext);
        when(BeanUtil.getBean(TrainRepository.class)).thenReturn(trainRepository);
    }

}
