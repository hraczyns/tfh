package com.hraczynski.trains.exceptions.definitions;

import com.hraczynski.trains.user.RegisterValidation;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
public class RegisterValidationException extends RuntimeException {
    private final List<RegisterValidation> validations;

    public RegisterValidationException(List<RegisterValidation> validations) {
        super("Cannot save user. Not all validation needs are met");
        this.validations = validations;
    }

    public RegisterValidationException(RegisterValidation validation) {
        super("Cannot save user. Not all validation needs are met");
        this.validations = Collections.singletonList(validation);
    }
}
