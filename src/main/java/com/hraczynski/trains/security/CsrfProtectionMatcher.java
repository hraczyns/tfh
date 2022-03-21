package com.hraczynski.trains.security;

import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

public class CsrfProtectionMatcher implements RequestMatcher {
    private static final List<String> ALLOWED_ENDPOINTS = List.of("/api/register", "/api/login");
    private static final String XSRF_TOKEN = "XSRF-TOKEN";
    private static final List<String> ALLOWED_METHODS = Arrays.asList("GET", "HEAD", "TRACE", "OPTIONS");

    @Override
    public boolean matches(HttpServletRequest httpServletRequest) {
        if (ALLOWED_METHODS.contains(httpServletRequest.getMethod())) return false;
        if (ALLOWED_ENDPOINTS.contains(httpServletRequest.getServletPath())) return false;

        Cookie[] cookies = httpServletRequest.getCookies();
        return cookies != null && Arrays.stream(cookies).anyMatch(c -> !XSRF_TOKEN.equals(c.getName()));
    }

    @Override
    public MatchResult matcher(HttpServletRequest request) {
        return RequestMatcher.super.matcher(request);
    }
}
