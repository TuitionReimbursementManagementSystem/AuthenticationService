package com.skillstorm.services;

import com.skillstorm.dtos.CredentialsDto;
import reactor.core.publisher.Mono;

public interface AuthUserService {

   // Login:
    Mono<String> login(CredentialsDto credentials);

    // Logout:
    Mono<Void> logout(String username);
}
