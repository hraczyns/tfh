package com.hraczynski.trains.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum RegisterValidation {
    NOT_HAVE_MIN_LENGTH("Password should has at least 8 chars"),
    NOT_CONTAINS_DEMANDED("Password should contain at least one special char"),
    NOT_CONTAINS_NUMBER("Password should contain at least one number"),
    NOT_CONTAINS_UPPERCASE("Password should contain at least one uppercase letter"),
    NOT_CONTAINS_LOWERCASE("Password should contain at least one lowercase letter"),
    NOT_CONTAINS_LETTER("Password should contain at least one letter"),
    INVALID_USERNAME("Username doesn't match email format"),
    EMPTY_INPUT("Username (email), password, name and surname must be defined"),
    DUPLICATE_USER("User already exists");

    private final String message;

    public static List<RegisterValidation> getUsernameAndPasswordValidations() {
        return Arrays.stream(values()).filter(err -> err != EMPTY_INPUT && err != DUPLICATE_USER).collect(Collectors.toList());
    }

}
