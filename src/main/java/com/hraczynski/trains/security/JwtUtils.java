package com.hraczynski.trains.security;

import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {
    private static String SECRET;

    @Value("${jwt.secret}")
    public void setSecret(String secretFetched) {
        JwtUtils.SECRET = secretFetched;
    }

    public static Algorithm getAlgorithm() {
        return Algorithm.HMAC512(SECRET);
    }

}
