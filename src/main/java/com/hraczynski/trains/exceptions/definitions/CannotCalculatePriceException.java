package com.hraczynski.trains.exceptions.definitions;

public class CannotCalculatePriceException extends RuntimeException {
    public CannotCalculatePriceException(String message) {
        super("Cannot perform calculation on the reservation due to: " + message);
    }
}
