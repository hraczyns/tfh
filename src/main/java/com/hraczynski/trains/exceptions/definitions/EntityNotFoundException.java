package com.hraczynski.trains.exceptions.definitions;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(Class<?> clazz, String... parameters) {
        super(clazz.getSimpleName() + " was not found for input parameters: " + String.join(" ", parameters));
    }

}
