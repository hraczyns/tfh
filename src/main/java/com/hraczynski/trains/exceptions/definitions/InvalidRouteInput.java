package com.hraczynski.trains.exceptions.definitions;

public class InvalidRouteInput extends RuntimeException{
    public InvalidRouteInput() {
        super("Route provided in input has uncompleted information.");
    }
}
