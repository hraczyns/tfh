package com.hraczynski.trains.routefinder;

import com.hraczynski.trains.journey.JourneyDTO;
import org.springframework.hateoas.CollectionModel;

import java.time.LocalDateTime;

public interface RouteFinderService {
    CollectionModel<JourneyDTO> findRoute(Long sourceId, Long destinationId, LocalDateTime startFindingTime);

    CollectionModel<JourneyDTO> findManyRoutes(Long sourceId, Long destinationId, LocalDateTime startFindingTime, int results);
}
