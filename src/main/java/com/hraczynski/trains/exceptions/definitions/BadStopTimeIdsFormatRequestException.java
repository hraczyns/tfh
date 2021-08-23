package com.hraczynski.trains.exceptions.definitions;

public class BadStopTimeIdsFormatRequestException extends RuntimeException {
    public BadStopTimeIdsFormatRequestException(String[] ids) {
        super("Provided format is not valid. Amount of ids has to be even number. Error in " + String.join(",", ids));
    }
    public BadStopTimeIdsFormatRequestException() {
        super("Provided format is not valid. Amount of ids has to be present.");
    }
}
