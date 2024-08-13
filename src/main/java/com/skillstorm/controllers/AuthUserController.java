package com.skillstorm.controllers;

import com.skillstorm.dtos.AuthUserDto;
import com.skillstorm.dtos.CredentialsDto;
import com.skillstorm.services.AuthUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/authorizations")
public class AuthUserController {

    private final AuthUserService authUserService;

    @Autowired
    public AuthUserController(AuthUserService authUserService) {
        this.authUserService = authUserService;
    }

    @PostMapping("/login")
    public Mono<AuthUserDto> login(@Valid @RequestBody CredentialsDto credentials) {
        return authUserService.login(credentials);
    }
}
