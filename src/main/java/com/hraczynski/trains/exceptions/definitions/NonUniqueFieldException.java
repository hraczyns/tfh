package com.hraczynski.trains.exceptions.definitions;

public class NonUniqueFieldException extends RuntimeException {
    public NonUniqueFieldException(Class<?> clazz, String fieldName, String fieldValue) {
        super("Field " + fieldName + " from " + clazz.getSimpleName() + " already exists in database. It has to be unique.");
    }
}
