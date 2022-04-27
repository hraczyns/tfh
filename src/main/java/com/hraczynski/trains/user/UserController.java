package com.hraczynski.trains.user;

import com.hraczynski.trains.security.jwt.JwtTokenAuthorizationFilter;
import com.hraczynski.trains.user.enabletoken.VerificationService;
import com.hraczynski.trains.user.register.RegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
@RequiredArgsConstructor
public class UserController {

    private final RegisterService registerService;
    private final VerificationService verificationService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AppUserRegisterRequest appUserRegisterRequest) {
        registerService.register(appUserRegisterRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/verification-token")
    public ResponseEntity<?> verifyToken(@RequestParam("token") String token) {
        verificationService.verifyToken(token, false);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * @see JwtTokenAuthorizationFilter
     */
    @GetMapping("/auth/check")
    public ResponseEntity<Void> isLogged() {
        return ResponseEntity.ok().build();
    }

    /**
     * @see JwtTokenAuthorizationFilter
     */
    @GetMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.ok().build();
    }
}