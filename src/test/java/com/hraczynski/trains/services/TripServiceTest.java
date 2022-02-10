package com.hraczynski.trains.services;

import com.hraczynski.trains.exceptions.definitions.EntityNotFoundException;
import com.hraczynski.trains.trip.Trip;
import com.hraczynski.trains.trip.TripService;
import com.hraczynski.trains.trip.TripServiceImpl;
import com.hraczynski.trains.trip.TripsRepository;
import com.hraczynski.trains.utils.BeanUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.hraczynski.trains.builders.TripTestBuilder.BasicTripEntity;
import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Trip service tests")
public class TripServiceTest {

    private static final Long ID = 1L;
    private static final Long TRAIN_ID = 10L;
    @Mock
    private TripsRepository repository;
    @Mock
    private ApplicationContext applicationContext;
    private TripService service;

    @BeforeEach
    public void setup() {
        service = new TripServiceImpl(repository);
    }

    @DisplayName("Get by id")
    @Test
    void getById() {
        //given
        Trip trip = givenTrip();
        when(repository.findById(trip.getId())).thenReturn(Optional.of(trip));
        whenBeanUtil();

        //when
        Trip result = service.getById(trip.getId());

        //then
        assertThat(result).isEqualTo(trip);
        verify(repository).findById(trip.getId());
    }

    @DisplayName("Doesn't get by id")
    @Test
    void notGetById() {
        //given
        when(repository.findById(ID)).thenReturn(Optional.empty());
        whenBeanUtil();

        //when
        //then
        assertThatCode(() -> service.getById(ID)).isInstanceOf(EntityNotFoundException.class);
    }

    @DisplayName("Get all")
    @Test
    void getAll() {
        //given
        Set<Trip> trips = givenSet();
        when(repository.findAll()).thenReturn(trips);

        //when
        Set<Trip> result = service.getAll();

        //then
        assertThat(result).isEqualTo(trips);
        verify(repository).findAll();
    }

    @DisplayName("Doesn't get all")
    @Test
    void notGetAll() {
        //given
        when(repository.findAll()).thenReturn(new HashSet<>());

        //when
        //then
        assertThatCode(() -> service.getAll()).isInstanceOf(EntityNotFoundException.class);
        verify(repository).findAll();
    }

    @DisplayName("Get all by train id")
    @Test
    void getAllByTrainId() {
        //given
        Set<Trip> trips = givenSet();
        when(repository.findTripByTrainId(TRAIN_ID)).thenReturn(trips);

        //when
        Set<Trip> result = service.getTripsByTrainId(TRAIN_ID);

        //then
        assertThat(result).isEqualTo(trips);
        verify(repository).findTripByTrainId(TRAIN_ID);
    }

    @DisplayName("Doesn't get all by train id")
    @Test
    void notGetAllByTrainId() {
        //given
        when(repository.findTripByTrainId(TRAIN_ID)).thenReturn(new HashSet<>());

        //when
        //then
        assertThatCode(() -> service.getTripsByTrainId(TRAIN_ID)).isInstanceOf(EntityNotFoundException.class);
        verify(repository).findTripByTrainId(TRAIN_ID);
    }

    private Set<Trip> givenSet() {
        return Collections.singleton(givenTrip());
    }

    private Trip givenTrip() {
        return make(a(BasicTripEntity));
    }

    @SuppressWarnings("all")
    private void whenBeanUtil() {
        BeanUtil beanUtil = new BeanUtil();
        beanUtil.setApplicationContext(applicationContext);
        when(BeanUtil.getBean(TripsRepository.class)).thenReturn(repository);
    }
}
