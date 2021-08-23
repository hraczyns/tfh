package com.hraczynski.trains.exceptions.definitions;

import java.time.LocalDateTime;

public class RouteNotExistException extends RuntimeException {
    public RouteNotExistException(Long source, Long destination, LocalDateTime dateTime) {
        super("Route was not found for input parameters: "
                + "source id = " + source + ", "
                + "destination id = " + destination + ", "
                + "with starting finding time = " + dateTime.toString());
    }

    public RouteNotExistException(String message) {
        super(message);
    }
}
