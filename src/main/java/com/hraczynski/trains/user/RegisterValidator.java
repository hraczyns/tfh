package com.hraczynski.trains.user;

import com.hraczynski.trains.exceptions.definitions.RegisterValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
@Slf4j
public class RegisterValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[.a-zA-Z0-9\\-_]+@[a-zA-Z0-9]+\\.[a-zA-Z0-9]{2,}$");
    private static final List<Character> DEMANDED_CHARACTERS_AT_LEAST_ONCE = List.of('-', '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', ';', ':', '?');
    private static final int MIN_LENGTH = 8;

    public void validate(AppUserRegisterRequest appUserRegisterRequest) {
        log.info("Validating user username and password");
        if (appUserRegisterRequest == null
                || appUserRegisterRequest.getUsername() == null
                || appUserRegisterRequest.getPassword() == null
                || appUserRegisterRequest.getName() == null
                || appUserRegisterRequest.getSurname() == null) {
            log.error("Empty input. Cannot create user");
            throw new RegisterValidationException(RegisterValidation.EMPTY_INPUT);
        }
        List<RegisterValidation> validations = RegisterValidation.getUsernameAndPasswordValidations();
        checkUsername(appUserRegisterRequest.getUsername(), validations);
        checkPassword(appUserRegisterRequest.getPassword(), validations);
        if (!validations.isEmpty()) {
            log.error("Username or email doesn't meet demands. Cannot create user.");
            throw new RegisterValidationException(validations);
        }
    }

    private void checkPassword(String password, List<RegisterValidation> registerValidations) {
        if (password.length() >= MIN_LENGTH) registerValidations.remove(RegisterValidation.NOT_HAVE_MIN_LENGTH);
        for (Character c : password.toCharArray()) {
            if (Character.isDigit(c)) registerValidations.remove(RegisterValidation.NOT_CONTAINS_NUMBER);
            if (Character.isUpperCase(c)) registerValidations.remove(RegisterValidation.NOT_CONTAINS_UPPERCASE);
            if (Character.isLowerCase(c)) registerValidations.remove(RegisterValidation.NOT_CONTAINS_LOWERCASE);
            if (Character.isLetter(c)) registerValidations.remove(RegisterValidation.NOT_CONTAINS_LETTER);
            if (DEMANDED_CHARACTERS_AT_LEAST_ONCE.contains(c))
                registerValidations.remove(RegisterValidation.NOT_CONTAINS_DEMANDED);
        }
    }

    private void checkUsername(String username, List<RegisterValidation> registerValidations) {
        if (EMAIL_PATTERN.matcher(username).matches()) {
            registerValidations.remove(RegisterValidation.INVALID_USERNAME);
        }
    }
}
