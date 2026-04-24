package br.com.dovalerio.cars_api.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expirationMinutes;

    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration-minutes}") long expirationMinutes
    ) {
        String normalizedSecret = normalizeSecret(secret);
        this.secretKey = Keys.hmacShaKeyFor(normalizedSecret.getBytes(StandardCharsets.UTF_8));
        this.expirationMinutes = expirationMinutes;
    }

    public String generateToken(UserDetails userDetails) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("roles", userDetails.getAuthorities()
                        .stream()
                        .map(Object::toString)
                        .toList())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expirationMinutes * 60)))
                .signWith(secretKey)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaims(normalizeToken(token)).getSubject();
    }

    public boolean isValid(String token, UserDetails userDetails) {
        Claims claims = extractClaims(normalizeToken(token));

        return claims.getSubject() != null
                && claims.getSubject().equals(userDetails.getUsername())
                && claims.getExpiration() != null
                && claims.getExpiration().after(new Date());
    }

    private Claims extractClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw new JwtException("Invalid JWT token", ex);
        }
    }

    private String normalizeSecret(String secret) {
        if (secret == null) {
            throw new IllegalStateException("JWT secret must not be null");
        }

        String normalized = secret.trim();
        if (normalized.isEmpty()) {
            throw new IllegalStateException("JWT secret must not be blank");
        }

        if (normalized.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("JWT secret must have at least 32 bytes");
        }

        return normalized;
    }

    private String normalizeToken(String token) {
        if (token == null) {
            throw new JwtException("Token is missing");
        }

        String normalized = token.trim();
        if (normalized.isEmpty()) {
            throw new JwtException("Token is missing");
        }

        return normalized;
    }
}