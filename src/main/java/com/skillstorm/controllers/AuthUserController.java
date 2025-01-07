package com.skillstorm.controllers;

import com.skillstorm.dtos.AuthUserDto;
import com.skillstorm.dtos.CredentialsDto;
import com.skillstorm.services.AuthUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/authentications")
public class AuthUserController {

    private final AuthUserService authUserService;

    @Autowired
    public AuthUserController(AuthUserService authUserService) {
        this.authUserService = authUserService;
    }

    // Test Endpoint:
    @GetMapping("/hello")
    public Mono<String> hello() {
        return Mono.just("Hello Authentication Service");
    }

    // Login:
    @PostMapping("/login")
    public Mono<String> login(@Valid @RequestBody Mono<CredentialsDto> credentials) {
        return credentials.flatMap(authUserService::login);
    }

    // Logout:
    @DeleteMapping("/logout")
    public Mono<Void> logout(@Header("username") String username) {
        return authUserService.logout(username);
    }
}
