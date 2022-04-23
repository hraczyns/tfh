package com.hraczynski.trains.exceptions.definitions.security;

public class JwtExistsInForbiddenStoreException extends RuntimeException{

    public JwtExistsInForbiddenStoreException(String message) {
        super(message);
    }
}
