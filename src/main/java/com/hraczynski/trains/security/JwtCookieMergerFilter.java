package com.hraczynski.trains.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Slf4j
@Component
public class JwtCookieMergerFilter extends OncePerRequestFilter {
    private static final String COOKIE_HEADER_PAYLOAD = "cookieHeaderPayload";
    private static final String COOKIE_SIGNATURE = "cookieSignature";
    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies == null) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }
        Cookie cookieHeaderPayload = null;
        Cookie cookieSignature = null;
        for (Cookie cookie : cookies) {
            String name = cookie.getName();
            if (COOKIE_HEADER_PAYLOAD.equalsIgnoreCase(name)) {
                cookieHeaderPayload = cookie;
            }
            if (COOKIE_SIGNATURE.equalsIgnoreCase(name)) {
                cookieSignature = cookie;
            }
        }
        if (cookieHeaderPayload == null || cookieSignature == null) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }
        String jwt = cookieHeaderPayload.getValue() + "." + cookieSignature.getValue();

        HeaderMapRequestWrapper requestWrapper = new HeaderMapRequestWrapper(httpServletRequest);
        requestWrapper.addHeader(AUTHORIZATION, BEARER + jwt);

        filterChain.doFilter(requestWrapper, httpServletResponse);
    }

    static class HeaderMapRequestWrapper extends HttpServletRequestWrapper {
        public HeaderMapRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        private final Map<String, String> headerMap = new HashMap<>();

        public void addHeader(String name, String value) {
            headerMap.put(name, value);
        }

        @Override
        public String getHeader(String name) {
            String headerValue = super.getHeader(name);
            if (headerMap.containsKey(name)) {
                headerValue = headerMap.get(name);
            }
            return headerValue;
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            List<String> names = Collections.list(super.getHeaderNames());
            names.addAll(headerMap.keySet());
            return Collections.enumeration(names);
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            List<String> values = Collections.list(super.getHeaders(name));
            if (headerMap.containsKey(name)) {
                values.add(headerMap.get(name));
            }
            return Collections.enumeration(values);
        }
    }
}
