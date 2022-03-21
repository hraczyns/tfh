package com.hraczynski.trains.user.enabletoken;

import com.hraczynski.trains.exceptions.definitions.EntityNotFoundException;
import com.hraczynski.trains.exceptions.definitions.IncoherentDataException;
import com.hraczynski.trains.exceptions.definitions.TokenExpiredException;
import com.hraczynski.trains.user.AppUser;
import com.hraczynski.trains.user.AppUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class VerificationService {

    private static final long HOURS_24 = 24 * 60 * 60;

    private final VerificationTokenRepository tokenRepository;
    private final VerificationTokenEmailService verificationTokenEmailService;
    private final AppUserRepository userRepository;

    public void sendVerificationEmailToUser(AppUser user) {
        String token = generateToken();
        createAndSaveTokenEntity(user, token);
        verificationTokenEmailService.sendVerificationEmail(user.getUsername(), token);
    }

    public void verifyToken(String token, boolean isAdminRequestToken) {
        log.info("Verifying token {}", token);
        VerificationToken verificationToken = getVerificationToken(token);
        LocalDateTime creationDate = verificationToken.getCreationDate();
        AppUser user = getUserByVerificationTokenOrDeleteTokenIfNotExist(verificationToken);
        if (isTokenExpired(creationDate)) {
            if (isAdminRequestToken) {
                log.warn("Deleting expired admin token");
            } else {
                log.warn("Deleting user (id = {}), activation token is expired", user.getId());
                userRepository.deleteById(user.getId());
            }
            tokenRepository.deleteById(verificationToken.getId());
            throw new TokenExpiredException();
        }

        user.setEnabled(true);
        userRepository.save(user);
        log.info("User (id = {}) is now unlocked", user.getId());

        tokenRepository.deleteById(verificationToken.getId());
        log.info("Used token has been deleted");
    }

    private AppUser getUserByVerificationTokenOrDeleteTokenIfNotExist(VerificationToken verificationToken) {
        AppUser user = verificationToken.getUser();
        if (user == null) {
            log.error("User stored in verificationToken does not exist");
            tokenRepository.deleteById(verificationToken.getId());
            throw new IncoherentDataException("User stored in verification token does not exist, please register again");
        }
        return user;
    }

    private VerificationToken getVerificationToken(String token) {
        VerificationToken verificationToken = tokenRepository.findByValue(token);
        if (verificationToken == null) {
            log.error("Token {} does not exist", token);
            throw new EntityNotFoundException(VerificationToken.class, "value = " + token);
        }
        return verificationToken;
    }

    private boolean isTokenExpired(LocalDateTime creationDate) {
        return Duration.between(creationDate, LocalDateTime.now()).toSeconds() > HOURS_24;
    }

    private void createAndSaveTokenEntity(AppUser user, String token) {
        VerificationToken verificationToken = new VerificationToken()
                .setUser(user)
                .setValue(token);
        tokenRepository.save(verificationToken);
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }

}
