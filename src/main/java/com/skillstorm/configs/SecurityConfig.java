package com.skillstorm.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) {
        http
                .csrf().disable()
                .authorizeExchange()
                .pathMatchers("/authentications/**").permitAll()
                .anyExchange().authenticated()
                .and()
                .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .oauth2ResourceServer()
                .jwt();

        return http.build();
    }

    // Jwt Decoder needed to be injected into the ServerHttpSecurity parameter in the securityWebFilterChain method above:
    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        String jwtSetUri = "i-don't-know-what-to-put-here.com";
        return NimbusReactiveJwtDecoder.withJwkSetUri(jwtSetUri).build();
    }

    // Password encoder to hash passwords instead of storing them as raw strings:
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
