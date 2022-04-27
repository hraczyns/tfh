package com.hraczynski.trains.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.*;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hraczynski.trains.exceptions.definitions.security.JwtExistsInForbiddenStoreException;
import com.hraczynski.trains.exceptions.definitions.security.UserNotExistsAuthenticationException;
import com.hraczynski.trains.user.UserService;
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
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenAuthorizationFilter extends OncePerRequestFilter {
    private static final String BEARER = "Bearer ";
    private static final String LOGIN_ENDPOINT = "/api/login";
    private static final String AUTH_CHECK_ENDPOINT = "/api/auth/check";
    private static final String LOGOUT_ENDPOINT = "/api/logout";
    private static final String SWAGGER_ENDPOINT_FRAGMENT = "/swagger";
    private static final String SWAGGER_ENDPOINT_FRAGMENT_2 = "/v2/api-docs";
    private static final String H2_CONSOLE_ENDPOINT_FRAGMENT = "/h2-console";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final JwtForbiddenTokensRepository jwtForbiddenTokensRepository;
    private final UserService userService;

    @Override
    @SuppressWarnings("unchecked")
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (allow(request.getServletPath())) {
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
            Map<String, Object> basicInfo = decodedJWT.getClaim("basicInfo").asMap();
            List<String> roles = (List<String>) basicInfo.get("roles");
            Collection<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            boolean exists = jwtForbiddenTokensRepository.existsByValue(token);
            if (exists) {
                log.error("Used invalidated token for user {}", username);
                throw new JwtExistsInForbiddenStoreException("Used invalidated token for user " + username);
            }

            boolean userNotExists = userService.getUserByUsername(username) == null;
            if (userNotExists) {
                log.error("User with given token does not exist. Invalidating jwt token {}", token);
                jwtForbiddenTokensRepository.save(new JwtForbiddenToken(null, convertDate(expiresAt), token));
                throw new UserNotExistsAuthenticationException("User " + username + " does not exists for given token");
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
                        "email", username,
                        "name", (String) basicInfo.get("name"),
                        "id", String.valueOf(basicInfo.get("id")),
                        "surname", (String) basicInfo.get("surname"),
                        "role", roles.toString()
                );
                objectMapper.writeValue(response.getOutputStream(), info);
            }

            filterChain.doFilter(request, response);
        } catch (UserNotExistsAuthenticationException | JwtExistsInForbiddenStoreException e) {
            prepareErrorResponse(response, e.getMessage());
        } catch (AlgorithmMismatchException e) {
            log.error("Algorithm mismatched when validating jwt");
            prepareErrorResponse(response, "Algorithm mismatched when validating jwt");
        } catch (InvalidClaimException e) {
            log.error("Invalid claim exception");
            prepareErrorResponse(response, "Invalid claim exception");
        } catch (JWTDecodeException e) {
            log.error("Error during decoding token");
            prepareErrorResponse(response, "Error during decoding token");
        } catch (JWTCreationException e) {
            log.error("Error during token creation");
            prepareErrorResponse(response, "Error during token creation");
        } catch (TokenExpiredException e) {
            log.error("Token is expired");
            prepareErrorResponse(response, "Token is expired");
        } catch (JWTVerificationException e) {
            log.error("Token is invalid");
            prepareErrorResponse(response, e.getMessage());
        }
    }

    private boolean allow(String servletPath) {
        return LOGIN_ENDPOINT.equals(servletPath) || servletPath.startsWith(SWAGGER_ENDPOINT_FRAGMENT) || servletPath.startsWith(H2_CONSOLE_ENDPOINT_FRAGMENT) || servletPath.startsWith(SWAGGER_ENDPOINT_FRAGMENT_2);
    }

    private void prepareErrorResponse(HttpServletResponse response, String msg) throws IOException {
        Map<String, String> error = Map.of(
                "error_message", msg);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        objectMapper.writeValue(response.getOutputStream(), error);
    }

    private LocalDateTime convertDate(Date expiresAt) {
        return Instant.ofEpochMilli(expiresAt.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
