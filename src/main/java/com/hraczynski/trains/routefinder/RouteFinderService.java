package com.hraczynski.trains.routefinder;

import com.hraczynski.trains.journey.JourneyDto;
import org.springframework.hateoas.CollectionModel;

import java.time.LocalDateTime;

public interface RouteFinderService {
    CollectionModel<JourneyDto> findRoute(Long sourceId, Long destinationId, LocalDateTime startFindingTime);

    CollectionModel<JourneyDto> findManyRoutes(Long sourceId, Long destinationId, LocalDateTime startFindingTime, int results);
}
