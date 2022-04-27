package com.hraczynski.trains.user.register;

import com.hraczynski.trains.exceptions.definitions.RegisterValidationException;
import com.hraczynski.trains.passengers.Passenger;
import com.hraczynski.trains.passengers.PassengerRequest;
import com.hraczynski.trains.passengers.PassengerService;
import com.hraczynski.trains.user.AppUser;
import com.hraczynski.trains.user.AppUserRegisterRequest;
import com.hraczynski.trains.user.AppUserRepository;
import com.hraczynski.trains.user.Role;
import com.hraczynski.trains.user.enabletoken.VerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegisterService {

    private final RegisterValidator registerValidator;
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationService verificationService;
    private final PassengerService passengerService;

    @Transactional
    public void register(AppUserRegisterRequest appUserRegisterRequest) {
        registerValidator.validate(appUserRegisterRequest);
        log.info("Register request for user {}", appUserRegisterRequest.getUsername());
        AppUser user = createUserAndPassenger(appUserRegisterRequest);
        checkIfUserExists(user.getUsername());
        appUserRepository.save(user);
        log.info("User {} has been created", user.getUsername());
        sendVerificationEmailToUser(user);
    }

    private void sendVerificationEmailToUser(AppUser user) {
        verificationService.sendVerificationEmailToUser(user);
    }

    private AppUser createUserAndPassenger(AppUserRegisterRequest appUserRegisterRequest) {
        Passenger passenger = passengerService.addPassenger(new PassengerRequest(null, appUserRegisterRequest.getName(), appUserRegisterRequest.getSurname(), appUserRegisterRequest.getUsername()));
        return new AppUser(null,
                appUserRegisterRequest.getUsername(),
                passwordEncoder.encode(appUserRegisterRequest.getPassword()),
                false,
                passenger,
                Role.USER
        );
    }

    private void checkIfUserExists(String username) {
        AppUser user = appUserRepository.findByUsername(username);
        if (user != null) {
            log.error("User {} already exists", username);
            throw new RegisterValidationException(RegisterValidation.DUPLICATE_USER);
        }
    }

}
