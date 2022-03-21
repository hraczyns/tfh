package com.hraczynski.trains.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenAuthorizationFilter extends OncePerRequestFilter {
    private static final String BEARER = "Bearer ";
    private static final String LOGIN_ENDPOINT = "/api/login";
    private static final String AUTH_CHECK_ENDPOINT = "/api/auth/check";
    private static final String LOGOUT_ENDPOINT = "/api/logout";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final JwtForbiddenTokensRepository jwtForbiddenTokensRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (LOGIN_ENDPOINT.equals(request.getServletPath())) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = authorizationHeader.substring(BEARER.length());
            Algorithm algorithm = JwtUtils.getAlgorithm();
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(token);
            Date expiresAt = decodedJWT.getExpiresAt();
            String username = decodedJWT.getSubject();
            String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
            Collection<SimpleGrantedAuthority> authorities = Arrays.stream(roles)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            boolean existsByValue = jwtForbiddenTokensRepository.existsByValue(token);
            if (existsByValue) {
                log.error("Used invalidated token for user {}", username);
                throw new RuntimeException("Used invalidated token for user " + username);
            }

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            if (LOGOUT_ENDPOINT.equals(request.getServletPath())) {
                jwtForbiddenTokensRepository.save(new JwtForbiddenToken(null, convertDate(expiresAt), token));
                filterChain.doFilter(request, response);
                return;
            }

            if (AUTH_CHECK_ENDPOINT.equals(request.getServletPath())) {
                Map<String, String> info = Map.of(
                        "username", username,
                        "role", Arrays.toString(roles)
                );
                objectMapper.writeValue(response.getOutputStream(), info);
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Error logging in: {}", e.getMessage());
            response.setStatus(FORBIDDEN.value());
            Map<String, String> error = Map.of(
                    "error_message", e.getMessage());
            response.setContentType(APPLICATION_JSON_VALUE);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            objectMapper.writeValue(response.getOutputStream(), error);
        }
    }

    private LocalDateTime convertDate(Date expiresAt) {
        return Instant.ofEpochMilli(expiresAt.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
