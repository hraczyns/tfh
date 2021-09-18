package com.hraczynski.trains.exceptions.definitions;

public class CannotBindToReservationException extends RuntimeException {
    public CannotBindToReservationException(String message) {
        super(message);
    }
}
