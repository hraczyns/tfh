package com.hraczynski.trains.exceptions.definitions;

public class CannotCreatePaymentException extends RuntimeException {
    public CannotCreatePaymentException(String message) {
        super(message);
    }
}
