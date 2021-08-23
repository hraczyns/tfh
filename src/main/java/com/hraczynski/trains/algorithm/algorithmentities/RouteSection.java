package com.hraczynski.trains.algorithm.algorithmentities;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import com.hraczynski.trains.city.City;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public abstract class RouteSection {
    private final City source;
    private final City destination;
}
