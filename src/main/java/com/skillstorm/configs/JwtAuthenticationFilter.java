package com.skillstorm.configs;

import com.skillstorm.exceptions.InvalidJwtException;
import com.skillstorm.services.AuthUserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

import java.util.Collections;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    private final AuthUserServiceImpl authUserService;

    @Autowired
    public JwtAuthenticationFilter(AuthUserServiceImpl authUserService) {
        this.authUserService = authUserService;
    }

    @Override
    @NonNull
    public Mono<Void> filter(ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if(bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            return authUserService.validateToken(token)
                    .flatMap(isValid -> {
                        if(!isValid) {
                            return Mono.error(new InvalidJwtException("Invalid Jwt Exception", new IllegalArgumentException()));
                        }
                        return authUserService.getUsernameFromToken(token)
                                .flatMap(username -> {
                                    Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
                                    return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
                                });
                    })
                    .onErrorResume(e -> Mono.error(new InvalidJwtException(e.getMessage(), e.getCause())));
        }
        return chain.filter(exchange);
    }
}
