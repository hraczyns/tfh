package com.hraczynski.trains.reservations;

import com.hraczynski.trains.exceptions.definitions.EntityNotFoundException;
import com.hraczynski.trains.journey.PartOfJourney;
import com.hraczynski.trains.journey.PartOfJourneyTimeTable;
import com.hraczynski.trains.passengers.*;
import com.hraczynski.trains.payment.Price;
import com.hraczynski.trains.payment.PriceDto;
import com.hraczynski.trains.payment.PriceMapper;
import com.hraczynski.trains.stoptime.StopTimeDto;
import com.hraczynski.trains.stoptime.StopTimeMapper;
import com.hraczynski.trains.train.Train;
import com.hraczynski.trains.train.TrainDto;
import com.hraczynski.trains.train.TrainRepository;
import com.hraczynski.trains.train.TrainRepresentationModelAssembler;
import com.hraczynski.trains.trip.Trip;
import com.hraczynski.trains.trip.TripController;
import com.hraczynski.trains.trip.TripsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
@Slf4j
public class ReservationRepresentationModelAssembler extends RepresentationModelAssemblerSupport<Reservation, ReservationDto> {

    private final StopTimeMapper stopTimeMapper;
    private final TripsRepository tripsRepository;
    private final PassengerRepresentationModelAssembler passengerRepresentationModelAssembler;
    private final PriceMapper priceMapper;
    private final PassengerNotRegisteredMapper passengerNotRegisteredMapper;
    private final TrainRepository trainRepository;
    private final TrainRepresentationModelAssembler trainRepresentationModelAssembler;

    public ReservationRepresentationModelAssembler(StopTimeMapper stopTimeMapper, TripsRepository tripsRepository, PassengerRepresentationModelAssembler passengerRepresentationModelAssembler, PriceMapper priceMapper, PassengerNotRegisteredMapper passengerNotRegisteredMapper, TrainRepository trainRepository, TrainRepresentationModelAssembler trainRepresentationModelAssembler) {
        super(ReservationController.class, ReservationDto.class);
        this.stopTimeMapper = stopTimeMapper;
        this.tripsRepository = tripsRepository;
        this.passengerRepresentationModelAssembler = passengerRepresentationModelAssembler;
        this.priceMapper = priceMapper;
        this.passengerNotRegisteredMapper = passengerNotRegisteredMapper;
        this.trainRepository = trainRepository;
        this.trainRepresentationModelAssembler = trainRepresentationModelAssembler;
    }

    @Override
    public ReservationDto toModel(Reservation entity) {
        ReservationDto reservationDto = instantiateModel(entity);
        reservationDto.setId(entity.getId());
        reservationDto.setReservationDate(entity.getReservationDate());
        reservationDto.setPrice(entity.getPrice());
        reservationDto.setStatus(entity.getStatus());
        reservationDto.setReservedRoute(getReservedRouteDtoAndAddLinks(stopTimeMapper.entitiesToDtos(entity.getReservedRoute()), reservationDto));
        reservationDto.setPricesInDetails(getPricesToDto(entity.getPrices()));
        reservationDto.setPassengerNotRegisteredList(passengerNotRegisteredMapper.deserialize(entity.getPassengersNotRegistered()));
        reservationDto.setPassengers(getPassengersToDtoAndAddLinks(entity.getPassengers(), reservationDto));
        reservationDto.setIdentifier(entity.getIdentifier());

        reservationDto.add(linkTo(methodOn(ReservationController.class).getById(entity.getId())).withSelfRel());
        reservationDto.add(linkTo(methodOn(ReservationController.class).getAll()).withRel("all"));
        return reservationDto;
    }

    private void addTripLink(ReservationDto reservationDto, StopTimeDto stopTimeDto) {
        Trip trip = tripsRepository.findTripByStopTimesId(stopTimeDto.getId()).orElseThrow(() -> {
            log.error("Cannot find trip by stop times id = {}", stopTimeDto.getId());
            return new EntityNotFoundException(Trip.class, "stopTimeId = " + stopTimeDto.getId());
        });
        if (!reservationDto.hasLink("trip_" + trip.getId())) {
            reservationDto.add(linkTo(methodOn(TripController.class).getById(trip.getId())).withRel("trip_" + trip.getId()));
        }
    }

    private List<PartOfJourney> getReservedRouteDtoAndAddLinks(List<StopTimeDto> stopTimeDtos, ReservationDto reservationDto) {
        List<PartOfJourney> stopTimeDtoPairElements = new ArrayList<>();
        for (int i = 1; i < stopTimeDtos.size(); i += 2) {
            StopTimeDto stopTimeDtoSource = stopTimeDtos.get(i - 1);
            StopTimeDto stopTimeDest = stopTimeDtos.get(i);
            Train train = trainRepository.findTrainByStopTimesId(stopTimeDtoSource.getId()).orElseThrow(() -> new EntityNotFoundException(Train.class, "stopTimeId = " + stopTimeDtoSource.getId()));
            TrainDto trainDto = trainRepresentationModelAssembler.toModel(train);
            stopTimeDtoPairElements.add(new PartOfJourneyTimeTable(stopTimeDtoSource, stopTimeDest, trainDto));
            addTripLink(reservationDto, stopTimeDtoSource);
            addTripLink(reservationDto, stopTimeDest);
        }
        return stopTimeDtoPairElements;
    }

    private Set<PassengerWithDiscountDto> getPassengersToDtoAndAddLinks(@NotNull Set<PassengerWithDiscount> passengers, ReservationDto reservationDto) {
        Set<PassengerWithDiscountDto> passengerWithDiscountDtos = new HashSet<>();
        for (PassengerWithDiscount passengerWithDiscount : passengers) {
            PassengerDto passengerDto = passengerRepresentationModelAssembler.toModel(passengerWithDiscount.getPassenger());
            PassengerWithDiscountDto passengerWithDiscountDto = new PassengerWithDiscountDto(passengerDto, passengerWithDiscount.getDiscount());
            passengerWithDiscountDtos.add(passengerWithDiscountDto);
            reservationDto.add(linkTo(methodOn(PassengerController.class).getById(passengerWithDiscount.getPassenger().getId())).withRel("passenger_" + passengerWithDiscount.getPassenger().getId()));
        }
        return passengerWithDiscountDtos;
    }

    @Override
    public CollectionModel<ReservationDto> toCollectionModel(Iterable<? extends Reservation> entities) {
        CollectionModel<ReservationDto> reservationDtoS = super.toCollectionModel(entities);
        reservationDtoS.add(linkTo(methodOn(ReservationController.class).getAll()).withSelfRel());
        return reservationDtoS;
    }

    private Set<PriceDto> getPricesToDto(Set<Price> prices) {
        return priceMapper.entitiesToDtos(prices);
    }
}
