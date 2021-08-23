package com.hraczynski.trains.algorithm.algorithmentities;

import com.hraczynski.trains.city.City;

import java.util.List;
import java.util.Objects;

public record RouteStops(List<City> stopList) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RouteStops that = (RouteStops) o;

        for (int i = 0; i < Math.min(that.stopList().size(), stopList.size()); i++) {
            if (!that.stopList.get(i).getId().equals(stopList.get(i).getId())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(stopList);
    }
}
