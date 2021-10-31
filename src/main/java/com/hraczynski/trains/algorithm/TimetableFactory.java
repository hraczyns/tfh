package com.hraczynski.trains.algorithm;

import com.hraczynski.trains.trip.TripsRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.hraczynski.trains.city.City;
import com.hraczynski.trains.algorithm.algorithmentities.RouteStops;
import com.hraczynski.trains.stoptime.StopTime;
import com.hraczynski.trains.trip.Trip;
import com.hraczynski.trains.stoptime.StopsTimeRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Getter
public class TimetableFactory {
    private final TripsRepository tripsRepository;
    private final StopsTimeRepository stopsTimeRepository;
    private RouteInfoProvider routeInfoProvider;
    private StopInfoProvider stopInfoProvider;
    private TripInfoProvider tripProvider;
    private TransferInfoProvider transferProvider;

    @Autowired
    public TimetableFactory(TripsRepository tripsRepository, StopsTimeRepository stopsTimeRepository) {
        this.tripsRepository = tripsRepository;
        this.stopsTimeRepository = stopsTimeRepository;
        createStopInfoProvider();
        createRouteAndTripInfoProvider();
        createTransferInfoProvider();
    }

    private void createTransferInfoProvider() {
        transferProvider = new TransferInfoProvider(new HashMap<>()); //not implemented
    }

    private void createStopInfoProvider() {
        List<StopTime> stopTimeList = stopsTimeRepository.findAll();
        stopInfoProvider = new StopInfoProvider(stopTimeList);
    }

    private void createRouteAndTripInfoProvider() {
        List<RouteStops> routeStopsList = new ArrayList<>();
        Set<Trip> tripList = tripsRepository.findAll();

        tripProvider = new TripInfoProvider(tripList);
        List<StopTime> stopTimes = stopsTimeRepository.findAll();
        stopTimes.stream()
                .collect(Collectors.groupingBy(StopTime::getTrip))
                .values()
                .forEach(s->{
                    List<City> collect = s.stream()
                            .map(StopTime::getStop)
                            .collect(Collectors.toList());
                    RouteStops routeStops = new RouteStops(collect);

                    if (!routeStopsList.contains(routeStops)) {
                        routeStopsList.add(routeStops);
                    }
                });
        routeInfoProvider = new RouteInfoProvider(routeStopsList);
    }
}
