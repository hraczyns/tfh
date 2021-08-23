package com.hraczynski.trains.exceptions.definitions;

public class ErrorDuringPropertiesCopying extends RuntimeException {
    public ErrorDuringPropertiesCopying(String simpleName, String simpleName1) {
        super("Error has occurred during copying from " + simpleName + " to " + simpleName1);
    }
}
