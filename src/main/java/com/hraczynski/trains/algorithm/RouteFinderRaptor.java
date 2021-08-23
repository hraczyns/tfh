package com.hraczynski.trains.algorithm;

import com.hraczynski.trains.algorithm.algorithmentities.*;
import com.hraczynski.trains.city.City;
import com.hraczynski.trains.city.CityRepository;
import com.hraczynski.trains.exceptions.definitions.EntityNotFoundException;
import com.hraczynski.trains.exceptions.definitions.RouteNotExistException;
import com.hraczynski.trains.stoptime.StopTime;
import com.hraczynski.trains.trip.Trip;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class RouteFinderRaptor {
    private final StopInfoProvider stopInfoProvider;
    private final RouteInfoProvider routeInfoProvider;
    private final TripInfoProvider tripInfoProvider;
    private final TransferInfoProvider transferInfoProvider;
    private final CityRepository cityRepository;

    @Autowired
    public RouteFinderRaptor(TimetableFactory creator, CityRepository cityRepository) {
        this.stopInfoProvider = creator.getStopInfoProvider();
        this.routeInfoProvider = creator.getRouteInfoProvider();
        this.tripInfoProvider = creator.getTripProvider();
        this.transferInfoProvider = creator.getTransferProvider();
        this.cityRepository = cityRepository;
    }

    public List<Journey> findRoute(Long sourceId, Long destinationId, LocalDateTime startFindingTime) {
        log.info("Looking for route from {} to {} with starting time {}", sourceId, destinationId, startFindingTime);
        City source = findCity(sourceId);
        City destination = findCity(destinationId);

        int round = 0;
        Set<City> markSet = new LinkedHashSet<>();
        Set<City> newMarkSet = new LinkedHashSet<>();
        markSet.add(source);
        Map<Integer, Map<City, LocalDateTime>> roundArrivals = new HashMap<>();
        roundArrivals.put(0, new HashMap<>());
        Map<City, Map<Integer, Results>> connections = new HashMap<>();


        stopInfoProvider.stopTimeList()
                .forEach(s -> {
                    roundArrivals.get(0).put(s.getStop(), LocalDateTime.MAX);
                    connections.put(s.getStop(), new HashMap<>());
                });

        roundArrivals.get(0).put(source, startFindingTime);

        Map<RouteStops, City> queue = new HashMap<>();

        while (!markSet.isEmpty()) {
            round++;

            queue.clear();
            Iterator<City> markIterator = markSet.iterator();
            while (markIterator.hasNext()) {
                City present = markIterator.next();
                List<RouteStops> routeStopsByStop = routeInfoProvider.getRouteStopsByStop(present);
                for (RouteStops routeStop : routeStopsByStop) {
                    if (queue.get(routeStop) == null || queue.get(routeStop) != null && routeInfoProvider.isStopBeforeInRoute(routeStop, present, queue.get(routeStop))) {
                        queue.put(routeStop, present);
                    }
                }
                markIterator.remove();
            }

            roundArrivals.put(round, new HashMap<>(roundArrivals.get(round - 1)));

            for (Map.Entry<RouteStops, City> routeStopsStopEntry : queue.entrySet()) {
                RouteStops route = routeStopsStopEntry.getKey();
                City city = routeStopsStopEntry.getValue();
                int boardingPoint = -1;
                Trip trip = null;

                int beginningStopIndex = routeInfoProvider.getIndexOfStopTimeByStop(route, city);
                for (int i = beginningStopIndex; i < routeInfoProvider.getSizeOfRoute(route); i++) {
                    City routeCity = route.stopList().get(i);
                    if (trip != null && trip.getStopTimes().get(i).getArrivalTime().isBefore(roundArrivals.get(round).get(routeCity))) {
                        roundArrivals.get(round).put(routeCity, trip.getStopTimes().get(i).getArrivalTime());
                        connections.get(routeCity).put(round, new Results(trip, boardingPoint, i));
                        newMarkSet.add(routeCity);
                    }

                    if (trip == null || roundArrivals.get(round - 1).get(routeCity).isBefore(trip.getStopTimes().get(i).getArrivalTime())) {
                        Optional<Trip> earliestTripAtStopOpt = tripInfoProvider.getEarliestTripAtStop(route, i, roundArrivals.get(round - 1).get(routeCity));
                        if (earliestTripAtStopOpt.isPresent()) {
                            trip = earliestTripAtStopOpt.get();
                        }
                        boardingPoint = i;
                    }
                }

            }
            for (City city : markSet) {
                for (Transfer transfer : transferInfoProvider.getTransfersForStop(city)) {
                    City dest = transfer.getDestination();
                    LocalDateTime arrivalTime = roundArrivals.get(round - 1).get(city).plus(transfer.getDuration());

                    if (arrivalTime.isBefore(roundArrivals.get(round).get(dest))) {
                        roundArrivals.get(round).put(dest, arrivalTime);
                        connections.get(dest).put(round, new Results(city, dest, transfer.getDuration(), transfer.getDistance()));
                        newMarkSet.add(dest);
                    }
                }
            }

            markSet = newMarkSet;


        }
        List<Journey> journeys = new ArrayList<>();

        for (int roundK : connections.get(destination).keySet()) {
            City dest = destination;
            List<RouteSection> routeSections = new ArrayList<>();

            while (roundK > 0) {
                Results connection = connections.get(dest).get(roundK);

                if (connection.getType() == ConnectionType.TRANSFER) {
                    routeSections.add(new Transfer(
                            connection.getOrigin(),
                            connection.getDestination(),
                            connection.getDuration(),
                            connection.getDistance()
                    ));

                    dest = connection.getOrigin();
                } else {
                    List<StopTime> stopTimes = connection.getTrip().getStopTimes();

                    City origin = stopTimes.get(connection.getBoardingPoint()).getStop();
                    dest = stopTimes.get(connection.getStopIndex()).getStop();

                    routeSections.add(new TimetableRouteSection(origin, dest, connection.getTrip()));

                    dest = stopTimes.get(connection.getBoardingPoint()).getStop();
                }

                roundK--;
            }

            Collections.reverse(routeSections);

            journeys.add(new Journey(journeys.size(), routeSections));
        }

        if (journeys.isEmpty()) {
            log.error("Cannot find any available route from {} to {} with starting time {}", source, destination, startFindingTime);
            throw new RouteNotExistException(sourceId, destinationId, startFindingTime);
        }


        return journeys; // todo investigate need of list, its always one elementary by now
    }

    private City findCity(Long id) {
        Optional<City> byId = cityRepository.findById(id);
        if (byId.isPresent()) {
            return byId.get();
        }
        log.error("Cannot find City with id = {}", id);
        throw new EntityNotFoundException(City.class, "id = " + id);
    }
}
