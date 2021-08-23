package com.hraczynski.trains.exceptions.definitions;

public class IncoherentDataException extends RuntimeException {
    public IncoherentDataException(Class<?> clazz1, Class<?> clazz2, String... args) {
        super("Provided data " + clazz1.getSimpleName() + " and " + clazz2.getSimpleName() + " with " + String.join(" ", args) + " are incoherent.");
    }
}
