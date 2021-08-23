package com.hraczynski.trains.utils;

import com.hraczynski.trains.city.City;
import com.hraczynski.trains.city.CityRequest;

public class DistanceCalculator {

    private final static double EARTH_RADIUS = 6372.8;

    public double calculate(City city, City city2) {
        return calc(city.getLat(), city2.getLat(), city.getLon(), city2.getLon());
    }

    public double calculate(CityRequest city, CityRequest city2) {
        return calc(city.getLat(), city2.getLat(), city.getLon(), city2.getLon());
    }

    private double calc(double lat1, double lat2, double lon1, double lon2) {
        double dLat = Math.toRadians(lat1 - lat2);
        double dLon = Math.toRadians(lon1 - lon2);
        double latRad2 = Math.toRadians(lat2);
        double latRad1 = Math.toRadians(lat1);

        double res1 = Math.pow(Math.sin(dLat / 2), 2)
                + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(latRad2) * Math.cos(latRad1);
        double res2 = 2 * Math.asin(Math.sqrt(res1));
        return EARTH_RADIUS * res2;
    }
}
