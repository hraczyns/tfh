package com.hraczynski.trains.scheduling;

import com.hraczynski.trains.security.JwtForbiddenToken;
import com.hraczynski.trains.security.JwtForbiddenTokensRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtForbiddenTokensRepoCleaner {

    private static final String EVERY_HOUR = "0 0 * * * *";

    private final JwtForbiddenTokensRepository jwtForbiddenTokensRepository;

    @Scheduled(cron = EVERY_HOUR)
    public void cleanFromExpiredTokens() {
        Set<JwtForbiddenToken> jwtForbiddenTokenSet = jwtForbiddenTokensRepository.findByExpiresAtAfter(LocalDateTime.now());
        if (!jwtForbiddenTokenSet.isEmpty()) {
            log.info("Found {} expired forbidden tokens. Deleting...", jwtForbiddenTokenSet.size());
            jwtForbiddenTokensRepository.deleteAll(jwtForbiddenTokenSet);
        } else {
            log.info("Not found any expired forbidden token.");
        }
    }
}
