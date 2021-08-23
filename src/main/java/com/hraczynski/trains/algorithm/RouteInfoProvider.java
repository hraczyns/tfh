package com.hraczynski.trains.algorithm;

import com.hraczynski.trains.algorithm.algorithmentities.RouteStops;
import com.hraczynski.trains.city.City;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public record RouteInfoProvider(List<RouteStops> routeStopsList) {

    public List<RouteStops> getRouteStopsByStop(City city) {
        return routeStopsList.stream()
                .filter(s -> s.stopList().contains(city))
                .collect(Collectors.toList());
    }

    public boolean isStopBeforeInRoute(RouteStops routeStop, City present, City found) {
        Optional<RouteStops> routeOpt = routeStopsList.stream()
                .filter(s -> s.equals(routeStop))
                .findFirst();
        if (routeOpt.isPresent()) {
            RouteStops routeStops = routeOpt.get();
            List<City> stopList = routeStops.stopList();
            Optional<City> first = stopList.stream().filter(s -> s.equals(present)).findFirst();
            Optional<City> second = stopList.stream().filter(s -> s.equals(found)).findFirst();
            if (first.isPresent() && second.isPresent()) {
                return stopList.indexOf(first.get()) < stopList.indexOf(second.get());
            }
        }
        return false;
    }

    public int getIndexOfStopTimeByStop(RouteStops routeStops, City city) {
        Optional<City> stopTimeOptional = routeStops.stopList().stream()
                .filter(s -> s.equals(city))
                .findFirst();
        return stopTimeOptional
                .map(city1 -> routeStops.stopList().indexOf(city1))
                .orElse(-1);
    }

    public int getSizeOfRoute(RouteStops routeStops) {
        return routeStopsList.stream()
                .filter(s -> s.equals(routeStops))
                .map(s -> s.stopList().size())
                .findFirst()
                .orElse(-1);
    }
}
