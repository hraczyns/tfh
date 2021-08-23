package com.hraczynski.trains.trip;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Service;
import com.hraczynski.trains.AbstractService;
import com.hraczynski.trains.exceptions.definitions.EntityNotFoundException;

import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class TripServiceImpl extends AbstractService<Trip, TripsRepository> implements TripService {
    private final TripsRepository tripsRepository;
    private final TripRepresentationModelAssembler assembler;

    public TripDTO getById(Long id) {
        log.info("Looking for Trip with id = {}", id);
        Trip entityById = getEntityById(id);
        return assembler.toModel(entityById);
    }

    @Override
    public CollectionModel<TripDTO> getAll() {
        log.info("Looking for all Trips");
        Set<Trip> all = tripsRepository.findAll();
        if (all == null || all.isEmpty()) {
            log.error("Cannot find any trips");
            throw new EntityNotFoundException(Trip.class, "none");
        }
        return assembler.toCollectionModel(all);
    }

    @Override
    public CollectionModel<TripDTO> getTripsByTrainId(Long id) {
        log.info("Looking for Trips by trainId = {}", id);
        Set<Trip> tripList = tripsRepository.findTripByTrainId(id);
        if (tripList == null || tripList.isEmpty()) {
            throw new EntityNotFoundException(Trip.class, "trainId = " + id);
        }
        return assembler.toCollectionModel(tripList);
    }
}
