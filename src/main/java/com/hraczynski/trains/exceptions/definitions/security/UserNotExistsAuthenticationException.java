package com.hraczynski.trains.exceptions.definitions.security;

public class UserNotExistsAuthenticationException extends RuntimeException{
    public UserNotExistsAuthenticationException(String message) {
        super(message);
    }
}
