package com.hraczynski.trains.security;

import com.hraczynski.trains.security.jwt.JwtCookieMergerFilter;
import com.hraczynski.trains.security.jwt.JwtTokenAuthenticationFilter;
import com.hraczynski.trains.security.jwt.JwtTokenAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Value("${frontend-page-url}")
    private String frontendPageUrl;
    private final UserDetailsService userDetailsService;
    private final JwtTokenAuthorizationFilter jwtTokenAuthorizationFilter;
    private final JwtCookieMergerFilter jwtCookieMergerFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
                .antMatchers("/h2-console/**", "/swagger-ui/**", "/swagger-resources/**", "/v2/api-docs");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .cors()
                .and()
                .csrf().requireCsrfProtectionMatcher(new CsrfProtectionMatcher()).csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .and()
                .headers().frameOptions().disable()
                .and()
                // general
                .authorizeRequests()
                .antMatchers(GET, "/api/cities/**", "/api/trips/**", "/api/trains/**", "/api/search/**", "/api/information/timetable/**").permitAll()
                .antMatchers("/api/cities/**", "/api/trips/**", "/api/trains/**").hasRole("ADMIN")
                .antMatchers("/api/payment/**","/payment/**").permitAll()
                // passengers
                .antMatchers(POST, "/api/passengers").permitAll()
                .antMatchers("/api/passengers/{passengerId}").access("hasRole('ADMIN') or (hasRole('USER') and @userSecurityCheck.hasPassengerId(authentication,#passengerId))")
                // reservations
                .antMatchers(POST, "api/reservations").permitAll()
                .antMatchers("/api/reservations/content**").permitAll()
                .antMatchers("/api/reservations/{reservationId}").access("hasRole('ADMIN') or (hasRole('USER') and @userSecurityCheck.hasPassengerIdByReservationId(authentication,#reservationId))")
                .antMatchers("/api/reservations/passengers/{passengerId}").access("hasRole('ADMIN') or (hasRole('USER') and @userSecurityCheck.hasPassengerId(authentication,#passengerId))")
                .antMatchers("/api/reservations/**").permitAll()
                // auth
                .antMatchers("/api/login", "/api/auth/check", "api/logout", "/api/register", "/api/verification-token").permitAll()
                //
                .anyRequest().authenticated()
                .and()
                .addFilter(new JwtTokenAuthenticationFilter(authenticationManagerBean()))
                .addFilterBefore(jwtTokenAuthorizationFilter, JwtTokenAuthenticationFilter.class)
                .addFilterBefore(jwtCookieMergerFilter, JwtTokenAuthorizationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin(frontendPageUrl);
        config.addAllowedHeader("*");
        config.addExposedHeader("*");
        config.addExposedHeader("Content-Disposition");
        config.addAllowedMethod("*"); // DO NOT DELETE THIS LINE! IT WILL HELP NEXT GENERATIONS!!! THIS IS ABSOLUTELY NECESSARY TO MAKE CORS WORK ON /login ENDPOINT!!! ABSOLUTELY
        config.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}