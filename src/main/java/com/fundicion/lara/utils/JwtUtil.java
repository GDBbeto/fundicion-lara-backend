package com.fundicion.lara.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private static final String SECRET = "MY_SUPER_SECRET_KEY_FOR_JWT_256_BIT_LONG_ENOUGH_123456";
    private static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 15; // 15 min in ms
    private static final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24; // 24h in ms

    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    public String generateAccessToken(String subject) {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String subject) {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public long getAccessTokenExpiration() {
        return ACCESS_TOKEN_EXPIRATION;
    }

    public long getRefreshTokenExpiration() {
        return REFRESH_TOKEN_EXPIRATION;
    }
}

