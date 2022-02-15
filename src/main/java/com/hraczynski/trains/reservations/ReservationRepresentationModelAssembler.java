package com.hraczynski.trains.reservations;

import com.hraczynski.trains.exceptions.definitions.EntityNotFoundException;
import com.hraczynski.trains.passengers.Passenger;
import com.hraczynski.trains.passengers.PassengerDto;
import com.hraczynski.trains.passengers.PassengerRepresentationModelAssembler;
import com.hraczynski.trains.payment.Price;
import com.hraczynski.trains.payment.PriceDto;
import com.hraczynski.trains.payment.PriceMapper;
import com.hraczynski.trains.trip.Trip;
import com.hraczynski.trains.trip.TripsRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;
import com.hraczynski.trains.passengers.PassengerController;
import com.hraczynski.trains.trip.TripController;
import com.hraczynski.trains.stoptime.StopTimeMapper;

import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
@Slf4j
public class ReservationRepresentationModelAssembler extends RepresentationModelAssemblerSupport<Reservation, ReservationDto> {

    private final ModelMapper mapper;
    private final StopTimeMapper stopTimeMapper;
    private final TripsRepository tripsRepository;
    private final PassengerRepresentationModelAssembler passengerRepresentationModelAssembler;
    private final PriceMapper priceMapper;


    @Autowired
    public ReservationRepresentationModelAssembler(ModelMapper mapper, StopTimeMapper stopTimeMapper, TripsRepository tripsRepository, PassengerRepresentationModelAssembler passengerRepresentationModelAssembler, PriceMapper priceMapper) {
        super(ReservationController.class, ReservationDto.class);
        this.mapper = mapper;
        this.stopTimeMapper = stopTimeMapper;
        this.tripsRepository = tripsRepository;
        this.passengerRepresentationModelAssembler = passengerRepresentationModelAssembler;
        this.priceMapper = priceMapper;
    }

    @Override
    public ReservationDto toModel(Reservation entity) {
        log.info("Transforming Reservation into model");
        ReservationDto reservationDto = instantiateModel(entity);
        mapper.map(entity, reservationDto);
        reservationDto.setReservedRoute(stopTimeMapper.entitiesToDtos(entity.getReservedRoute()));
        reservationDto.setPricesInDetails(getPricesToDto(entity.getPrices()));
        reservationDto.setPassengers(getPassengersToDto(entity.getPassengers()));
        reservationDto.setIdentifier(entity.getIdentifier());
        reservationDto.add(linkTo(methodOn(ReservationController.class).getById(entity.getId())).withSelfRel());
        reservationDto.add(linkTo(methodOn(ReservationController.class).getAll()).withRel("all"));
        reservationDto.getPassengers().forEach(s -> reservationDto.add(linkTo(methodOn(PassengerController.class).getById(s.getId())).withRel("passenger_" + s)));
        reservationDto.getReservedRoute().forEach(s -> {
            Trip trip = tripsRepository.findTripByStopTimesId(s.getId()).orElseThrow(() -> {
                log.error("Cannot find trip by stop times id = {}", s.getId());
                return new EntityNotFoundException(Trip.class, "stopTimeId = " + s.getId());
            });
            if (!reservationDto.hasLink("trip_" + trip.getId())) {
                reservationDto.add(linkTo(methodOn(TripController.class).getById(trip.getId())).withRel("trip_" + trip.getId()));
            }
        });
        return reservationDto;
    }

    private Set<PassengerDto> getPassengersToDto(@NotNull Set<Passenger> passengers) {
        return passengers
                .stream()
                .map(passengerRepresentationModelAssembler::toModel)
                .collect(Collectors.toSet());
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
