package com.hraczynski.trains.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final AppUserRepository appUserRepository;

    public AppUser getUserByUsername(String username) {
        log.info("Looking for user with username {}", username);
        AppUser user = appUserRepository.findByUsername(username);
        if (user == null) {
            log.error("User with username {} not found", username);
        }
        return user;
    }
}
