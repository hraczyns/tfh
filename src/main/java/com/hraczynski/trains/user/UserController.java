package com.hraczynski.trains.user;

import com.hraczynski.trains.user.enabletoken.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
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
     * @see com.hraczynski.trains.security.JwtTokenAuthorizationFilter
     */
    @GetMapping("/auth/check")
    public ResponseEntity<Void> isLogged() {
        return ResponseEntity.ok().build();
    }

    /**
     * @see com.hraczynski.trains.security.JwtTokenAuthorizationFilter
     */
    @GetMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.ok().build();
    }
}