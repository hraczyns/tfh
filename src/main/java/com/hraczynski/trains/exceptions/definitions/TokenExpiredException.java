package com.hraczynski.trains.exceptions.definitions;

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException() {
        super("Token is expired, please regenerate.");
    }
}
