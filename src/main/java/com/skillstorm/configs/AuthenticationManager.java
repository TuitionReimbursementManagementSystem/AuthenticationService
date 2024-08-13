package com.skillstorm.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final JWTConfig jwtConfig;

    @Autowired
    public AuthenticationManager(JWTConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();

        return Mono.defer(() ->
                jwtConfig.getUsernameFromToken(token)
                        .flatMap(username -> jwtConfig.validate(token)
                                .filter(isValid -> isValid)
                                .flatMap(isValid -> jwtConfig.getClaimsFromToken(token)
                                        .map(claims -> {
                                            List<String> roles = claims.get("role", List.class);
                                            return new UsernamePasswordAuthenticationToken(
                                                    username,
                                                    null,
                                                    roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
                                            );
                                        })
                                )
                        ).switchIfEmpty(Mono.error(new UsernameNotFoundException("Invalid token or username not found")))
        );
    }


}
