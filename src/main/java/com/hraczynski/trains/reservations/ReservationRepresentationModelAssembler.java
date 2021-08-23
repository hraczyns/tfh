package com.hraczynski.trains.reservations;

import com.hraczynski.trains.exceptions.definitions.EntityNotFoundException;
import com.hraczynski.trains.passengers.Passenger;
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

import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
@Slf4j
public class ReservationRepresentationModelAssembler extends RepresentationModelAssemblerSupport<Reservation, ReservationDTO> {

    private final ModelMapper mapper;
    private final StopTimeMapper stopTimeMapper;
    private final TripsRepository tripsRepository;


    @Autowired
    public ReservationRepresentationModelAssembler(ModelMapper mapper, StopTimeMapper stopTimeMapper, TripsRepository tripsRepository) {
        super(ReservationsController.class, ReservationDTO.class);
        this.mapper = mapper;
        this.stopTimeMapper = stopTimeMapper;
        this.tripsRepository = tripsRepository;
    }

    @Override
    public ReservationDTO toModel(Reservation entity) {
        log.info("Transforming Reservation {} into model", entity);
        ReservationDTO reservationDTO = instantiateModel(entity);
        mapper.map(entity, reservationDTO);
        reservationDTO.setReservedRoute(stopTimeMapper.entitiesToDTOs(entity.getReservedRoute()));
//        reservationDTO.setDiscount(entity.g); //fixme
        reservationDTO.setIdPassengers(entity.getPassengers().stream().map(Passenger::getId).collect(Collectors.toSet()));
        reservationDTO.add(linkTo(methodOn(ReservationsController.class).getById(entity.getId())).withSelfRel());
        reservationDTO.add(linkTo(methodOn(ReservationsController.class).getAll()).withRel("all"));
        reservationDTO.getIdPassengers().forEach(s -> reservationDTO.add(linkTo(methodOn(PassengerController.class).getById(s)).withRel("passenger_" + s)));
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

    @Override
    public CollectionModel<ReservationDTO> toCollectionModel(Iterable<? extends Reservation> entities) {
        CollectionModel<ReservationDTO> reservationDTOS = super.toCollectionModel(entities);
        reservationDTOS.add(linkTo(methodOn(ReservationsController.class).getAll()).withSelfRel());
        return reservationDTOS;
    }
}
