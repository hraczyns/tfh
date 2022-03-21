package com.hraczynski.trains.exceptions.responses;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RegisterValidationError extends ApiSubError {
    private final String name;
}
