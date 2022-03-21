package com.hraczynski.trains.security;

import com.hraczynski.trains.exceptions.definitions.EntityNotFoundException;
import com.hraczynski.trains.user.AppUser;
import com.hraczynski.trains.user.AppUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SimpleUserDetailsService implements UserDetailsService {

    private final AppUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Attempting to find user: {}", username);
        AppUser user = userRepository.findByUsername(username);
        if (user == null) {
            log.error("Cannot find user: {}", username);
            throw new EntityNotFoundException(AppUser.class, "username = " + username);
        }
        return user;
    }
}
