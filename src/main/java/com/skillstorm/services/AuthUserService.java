package com.skillstorm.services;

import com.skillstorm.dtos.AuthUserDto;
import com.skillstorm.dtos.CredentialsDto;
import reactor.core.publisher.Mono;

public interface AuthUserService {

   // Login:
    Mono<AuthUserDto> login(CredentialsDto credentials);
}
