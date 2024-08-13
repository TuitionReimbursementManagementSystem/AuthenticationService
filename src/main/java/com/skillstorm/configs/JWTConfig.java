package com.skillstorm.configs;

import com.skillstorm.entities.AuthUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.security.Key;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTConfig {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.duration}")
    private String durationSeconds;

    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public Mono<Claims> getClaimsFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return Mono.just(claims);
        } catch (JwtException | IllegalArgumentException e) {
            return Mono.error(new RuntimeException("JWT Invalid"));
        }
    }

    public Mono<String> getUsernameFromToken(String token) {
        return getClaimsFromToken(token)
                .map(Claims::getSubject);
    }

    public Mono<LocalDate> getExpirationDateFromToken(String token) {
        return getClaimsFromToken(token)
                .map(claims -> {
                    Date expirationDate = claims.getExpiration();
                    return expirationDate
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                });
    }

    private Mono<Boolean> isTokenExpired(String token) {
        return getExpirationDateFromToken(token)
                .map(expirationDate -> expirationDate.isBefore(LocalDate.now()));
    }

    public Mono<String> generateToken(AuthUser user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRoles());
        return Mono.fromCallable(() -> doGenerateToken(claims, user.getUsername()));
    }

    private String doGenerateToken(Map<String, Object> claims, String username) {
        Long durationsSecondsAsLong;
        try {
            durationsSecondsAsLong = Long.parseLong(durationSeconds);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid duration format in JWT", e);
        }

        final LocalDate createdDate = LocalDate.now();
        final LocalDate expirationDate = createdDate.plusDays(durationsSecondsAsLong / 86400);

        ZonedDateTime zonedDateExpirationTime = expirationDate.atStartOfDay(ZoneId.systemDefault());
        Date nonZonedExpirationDate = Date.from(zonedDateExpirationTime.toInstant());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(nonZonedExpirationDate)
                .signWith(key)
                .compact();
    }

    // A little confusing with the Mono, but we check if the token is expired and return true (yes validate) if it's not
    // or false (do not validate) if it is expired:
    public Mono<Boolean> validate(String token) {
        return isTokenExpired(token)
                .map(isExpired -> !isExpired);
    }
}
