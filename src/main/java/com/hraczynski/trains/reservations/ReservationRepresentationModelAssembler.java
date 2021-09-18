package com.hraczynski.trains.reservations;

import com.hraczynski.trains.exceptions.definitions.EntityNotFoundException;
import com.hraczynski.trains.passengers.Passenger;
import com.hraczynski.trains.passengers.PassengerDTO;
import com.hraczynski.trains.passengers.PassengerRepresentationModelAssembler;
import com.hraczynski.trains.payment.Price;
import com.hraczynski.trains.payment.PriceDTO;
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
public class ReservationRepresentationModelAssembler extends RepresentationModelAssemblerSupport<Reservation, ReservationDTO> {

    private final ModelMapper mapper;
    private final StopTimeMapper stopTimeMapper;
    private final TripsRepository tripsRepository;
    private final PassengerRepresentationModelAssembler passengerRepresentationModelAssembler;
    private final PriceMapper priceMapper;


    @Autowired
    public ReservationRepresentationModelAssembler(ModelMapper mapper, StopTimeMapper stopTimeMapper, TripsRepository tripsRepository, PassengerRepresentationModelAssembler passengerRepresentationModelAssembler, PriceMapper priceMapper) {
        super(ReservationController.class, ReservationDTO.class);
        this.mapper = mapper;
        this.stopTimeMapper = stopTimeMapper;
        this.tripsRepository = tripsRepository;
        this.passengerRepresentationModelAssembler = passengerRepresentationModelAssembler;
        this.priceMapper = priceMapper;
    }

    @Override
    public ReservationDTO toModel(Reservation entity) {
        log.info("Transforming Reservation into model");
        ReservationDTO reservationDTO = instantiateModel(entity);
        mapper.map(entity, reservationDTO);
        reservationDTO.setReservedRoute(stopTimeMapper.entitiesToDTOs(entity.getReservedRoute()));
        reservationDTO.setPricesInDetails(getPricesToDTO(entity.getPrices()));
        reservationDTO.setPassengers(getPassengersToDTO(entity.getPassengers()));
        reservationDTO.add(linkTo(methodOn(ReservationController.class).getById(entity.getId())).withSelfRel());
        reservationDTO.add(linkTo(methodOn(ReservationController.class).getAll()).withRel("all"));
        reservationDTO.getPassengers().forEach(s -> reservationDTO.add(linkTo(methodOn(PassengerController.class).getById(s.getId())).withRel("passenger_" + s)));
        reservationDTO.getReservedRoute().forEach(s -> {
            Trip trip = tripsRepository.findTripByStopTimesId(s.getId()).orElseThrow(() -> {
                log.error("Cannot find trip by stop times id = {}", s.getId());
                return new EntityNotFoundException(Trip.class, "stopTimeId = " + s.getId());
            });
            if (!reservationDTO.hasLink("trip_" + trip.getId())) {
                reservationDTO.add(linkTo(methodOn(TripController.class).getById(trip.getId())).withRel("trip_" + trip.getId()));
            }
        });
        return reservationDTO;
    }

    private Set<PassengerDTO> getPassengersToDTO(@NotNull Set<Passenger> passengers) {
        return passengers
                .stream()
                .map(passengerRepresentationModelAssembler::toModel)
                .collect(Collectors.toSet());
    }

    @Override
    public CollectionModel<ReservationDTO> toCollectionModel(Iterable<? extends Reservation> entities) {
        CollectionModel<ReservationDTO> reservationDTOS = super.toCollectionModel(entities);
        reservationDTOS.add(linkTo(methodOn(ReservationController.class).getAll()).withSelfRel());
        return reservationDTOS;
    }

    private Set<PriceDTO> getPricesToDTO(Set<Price> prices) {
        return priceMapper.entitiesToDTOs(prices);
    }
}
