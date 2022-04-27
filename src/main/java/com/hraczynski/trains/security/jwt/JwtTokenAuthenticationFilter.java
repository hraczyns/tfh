package com.hraczynski.trains.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.hraczynski.trains.passengers.Passenger;
import com.hraczynski.trains.user.AppUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class JwtTokenAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private static final Long HOURS_24 = 24 * 60 * 60 * 1000L;
    private static final int HOURS_24_SEC = 24 * 60 * 60;

    public JwtTokenAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        setFilterProcessesUrl("/api/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {
        AppUser user = (AppUser) authResult.getPrincipal();
        Passenger passenger = user.getPassenger();
        Algorithm algorithm = JwtUtils.getAlgorithm();
        String accessToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + HOURS_24))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("basicInfo", Map.of(
                        "id", passenger.getId(),
                        "name", passenger.getName(),
                        "surname", passenger.getSurname(),
                        "email", passenger.getEmail(),
                        "roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList())
                ))
                .sign(algorithm);

        String contextPath = request.getContextPath();
        String cookiePath = contextPath.length() > 0 ? contextPath : "/";

        String[] tokenParts = accessToken.split("\\.");

        Cookie cookieHeaderPayload = new Cookie("cookieHeaderPayload", tokenParts[0] + "." + tokenParts[1]);
        cookieHeaderPayload.setSecure(true);
        cookieHeaderPayload.setMaxAge(60 * 30 * 60);
        cookieHeaderPayload.setHttpOnly(false);
        cookieHeaderPayload.setPath(cookiePath);

        Cookie cookieSignature = new Cookie("cookieSignature", tokenParts[2]);
        cookieSignature.setSecure(true);
        cookieSignature.setHttpOnly(true);
        cookieSignature.setPath(cookiePath);
        cookieSignature.setMaxAge(HOURS_24_SEC);

        response.addCookie(cookieHeaderPayload);
        response.addCookie(cookieSignature);
        response.setContentType(APPLICATION_JSON_VALUE);
        log.info("User {} successfully authenticated!", user.getUsername());
    }

}