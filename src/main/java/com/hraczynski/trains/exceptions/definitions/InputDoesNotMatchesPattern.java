package com.hraczynski.trains.exceptions.definitions;

public class InputDoesNotMatchesPattern extends RuntimeException {
    public InputDoesNotMatchesPattern(String input, String sample) {
        super("Input " + input + " doesn't match pattern! Valid example " + sample);
    }
}
