package com.hraczynski.trains.security;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Set;

public interface JwtForbiddenTokensRepository extends JpaRepository<JwtForbiddenToken, String> {
    boolean existsByValue(String value);
    Set<JwtForbiddenToken> findByExpiresAtAfter(LocalDateTime localDateTime);
}
