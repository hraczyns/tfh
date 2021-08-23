package com.hraczynski.trains.routefinder;

import com.hraczynski.trains.algorithm.RouteFinderRaptor;
import com.hraczynski.trains.algorithm.algorithmentities.Journey;
import com.hraczynski.trains.exceptions.definitions.RouteNotExistException;
import com.hraczynski.trains.journey.JourneyDTO;
import com.hraczynski.trains.journey.JourneyRepresentationModelAssembler;
import com.hraczynski.trains.stoptime.StopTime;
import com.hraczynski.trains.stoptime.StopsTimeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RouteFinderServiceImpl implements RouteFinderService {
    private final RouteFinderRaptor routeFinderRaptor;
    private final JourneyRepresentationModelAssembler assembler;
    private final StopsTimeRepository stopsTimeRepository;

    @Override
    public CollectionModel<JourneyDTO> findRoute(Long sourceId, Long destinationId, LocalDateTime startFindingTime) {
        return assembler.toCollectionModel(routeFinderRaptor.findRoute(sourceId, destinationId, startFindingTime));
    }

    @Override
    public CollectionModel<JourneyDTO> findManyRoutes(Long sourceId, Long destinationId, LocalDateTime startFindingTime, int results) {

        List<LocalDateTime> arrivalDatesByNumberOfResultsAndCity = getArrivalDatesByNumberOfResultsAndCity(sourceId, results, startFindingTime);
        List<Journey> resultFromAlgorithm;

        ExecutorService executorService = Executors.newCachedThreadPool(); // at the first glance unnecessary complex code, but execution sped up 3-4 times
        try {
            List<Future<List<Journey>>> futures = executorService.invokeAll(
                    arrivalDatesByNumberOfResultsAndCity.stream()
                            .map(date -> (Callable<List<Journey>>) () -> routeFinderRaptor.findRoute(sourceId, destinationId, date))
                            .collect(Collectors.toList())
            );
            AtomicInteger resultId = new AtomicInteger(1);
            resultFromAlgorithm = futures.stream()
                    .map(s -> {
                        try {
                            return s.get();
                        } catch (InterruptedException | ExecutionException e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .flatMap(Collection::stream)
                    .map(journey -> new Journey(resultId.getAndIncrement(), journey.sections()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            resultFromAlgorithm = Collections.emptyList();
        } finally {
            executorService.shutdown();
        }

        return assembler.toCollectionModel(resultFromAlgorithm);

    }

    public List<LocalDateTime> getArrivalDatesByNumberOfResultsAndCity(Long cityId, int results, LocalDateTime startTime) {
        log.info("Looking for {} StopTimes - CityId: {} ordered desc by arrival time starting from {}", results, cityId, startTime);
        List<LocalDateTime> localDateTimes = stopsTimeRepository.findByCityIdAndArrivalTimeGreaterThanOrderByArrivalTime(cityId, startTime)
                .stream()
                .limit(results)
                .map(StopTime::getArrivalTime)
                .collect(Collectors.toList());
        if (!localDateTimes.isEmpty()) {
            return localDateTimes;
        }
        log.error("Error during finding route results.");
        throw new RouteNotExistException("Cannot establish one of finding results");
    }
}
