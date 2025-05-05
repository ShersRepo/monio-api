package com.smart_tiger.monio.middleware.security.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SecurityException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static com.smart_tiger.monio.middleware.security.SecurityConstants.JWT_EXPIRATION_MINUTES;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    private final JwtKeyManager keyManager;

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(JWT_EXPIRATION_MINUTES, ChronoUnit.MINUTES)))
                .header().add("kid", keyManager.getCurrentKeyAlias())
                .and()
                .signWith(keyManager.getCurrentKey())
                .compact();
    }

    public Authentication validateToken(String token) {
        try {
            Claims claims = getTokenClaim(token);
            String userName = claims.getSubject();
            return new UsernamePasswordAuthenticationToken(userName, null, null);
        } catch (JwtException e) {
            throw new SecurityException("Invalid JWT token", e);
        }
    }

    public String getUsernameFromToken(String token) {
        try {
            Claims claims = getTokenClaim(token);
            return claims.getSubject();
        } catch (JwtException e) {
            throw new SecurityException("Invalid user from JWT token", e);
        }
    }

    private Claims getTokenClaim(String token) throws JwtException {
        return Jwts.parser()
                .keyLocator(header -> {
                    String keyId = (String) header.get("kid");
                    return keyManager.getKey(keyId);
                })
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
