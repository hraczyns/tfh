package com.hraczynski.trains.routefinder;

import com.hraczynski.trains.algorithm.RouteFinderRaptor;
import com.hraczynski.trains.algorithm.algorithmentities.Journey;
import com.hraczynski.trains.algorithm.algorithmentities.RouteSection;
import com.hraczynski.trains.algorithm.algorithmentities.TimetableRouteSection;
import com.hraczynski.trains.city.City;
import com.hraczynski.trains.exceptions.definitions.EntityNotFoundException;
import com.hraczynski.trains.journey.JourneyDto;
import com.hraczynski.trains.journey.JourneyRepresentationModelAssembler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RouteFinderServiceImpl implements RouteFinderService {
    private final RouteFinderRaptor routeFinderRaptor;
    private final JourneyRepresentationModelAssembler assembler;

    @Override
    public CollectionModel<JourneyDto> findRoute(Long sourceId, Long destinationId, LocalDateTime startFindingTime) {
        return assembler.toCollectionModel(routeFinderRaptor.findRoute(sourceId, destinationId, startFindingTime));
    }

    @Override
    public CollectionModel<JourneyDto> findManyRoutes(Long sourceId, Long destinationId, LocalDateTime startFindingTime, int results) {
        AtomicInteger id = new AtomicInteger(1);
        List<Journey> route = routeFinderRaptor.findRoute(sourceId, destinationId, startFindingTime)
                .stream()
                .map(s -> new Journey(id.get(), s.sections()))
                .collect(Collectors.toList());
        id.incrementAndGet();

        for (int i = 0; i < results - 1; i++) {
            RouteSection routeSection = route.get(route.size() - 1).sections().get(0);
            if (routeSection instanceof TimetableRouteSection) {
                LocalDateTime departureTime = ((TimetableRouteSection) routeSection).getTrip().getStopTimes().stream()
                        .filter(s -> s.getStop().getId().equals(sourceId))
                        .findFirst()
                        .orElseThrow(() -> new EntityNotFoundException(City.class, "id = " + sourceId))
                        .getDepartureTime();
                List<Journey> next = routeFinderRaptor.findRoute(sourceId, destinationId, departureTime.plusMinutes(1));
                next = next.stream()
                        .map(s -> new Journey(id.get(), s.sections()))
                        .collect(Collectors.toList());
                route.addAll(next);
                id.incrementAndGet();
            }
        }
        return assembler.toCollectionModel(route);
    }

//    @Override
//    public CollectionModel<JourneyDto> findManyRoutes(Long sourceId, Long destinationId, LocalDateTime startFindingTime, int results) {
//
//        List<LocalDateTime> arrivalDatesByNumberOfResultsAndCity = getArrivalDatesByNumberOfResultsAndCity(sourceId, results, startFindingTime);
//        List<Journey> resultFromAlgorithm;
//
//        ExecutorService executorService = Executors.newCachedThreadPool(); // at the first glance unnecessary complex code, but execution sped up 3-4 times
//        try {
//            List<Future<List<Journey>>> futures = executorService.invokeAll(
//                    arrivalDatesByNumberOfResultsAndCity.stream()
//                            .map(date -> (Callable<List<Journey>>) () -> routeFinderRaptor.findRoute(sourceId, destinationId, date))
//                            .collect(Collectors.toList())
//            );
//            AtomicInteger resultId = new AtomicInteger(1);
//            resultFromAlgorithm = futures.stream()
//                    .map(s -> {
//                        try {
//                            return s.get();
//                        } catch (InterruptedException | ExecutionException e) {
//                            return null;
//                        }
//                    })
//                    .filter(Objects::nonNull)
//                    .flatMap(Collection::stream)
//                    .map(journey -> new Journey(resultId.getAndIncrement(), journey.sections()))
//                    .collect(Collectors.toList());
//        } catch (Exception e) {
//            resultFromAlgorithm = Collections.emptyList();
//        } finally {
//            executorService.shutdown();
//        }
//
//
//        return assembler.toCollectionModel(resultFromAlgorithm);
//
//    }
//
//    private List<LocalDateTime> getArrivalDatesByNumberOfResultsAndCity(Long cityId, int results, LocalDateTime startTime) {
//        log.info("Looking for StopTimes - CityId: {} ordered desc by arrival time starting from {}", cityId, startTime);
//        List<LocalDateTime> localDateTimes = stopsTimeRepository.findByCityIdAndArrivalTimeGreaterThanOrderByArrivalTime(cityId, startTime)
//                .stream()
//                .filter(DistinctByField.distinctByKey(StopTime::getArrivalTime))
//                .limit(results)
//                .map(StopTime::getArrivalTime)
//                .collect(Collectors.toList());
//        if (!localDateTimes.isEmpty()) {
//            return localDateTimes;
//        }
//        log.error("Error during finding route results.");
//        throw new RouteNotExistException("Cannot establish one of finding results");
//    }

}
