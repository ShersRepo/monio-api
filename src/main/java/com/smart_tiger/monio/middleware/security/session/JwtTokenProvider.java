package com.smart_tiger.monio.middleware.security.session;

import com.smart_tiger.monio.middleware.exception.SessionTerminatedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SecurityException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static com.smart_tiger.monio.middleware.security.SecurityConstants.JWT_EXPIRATION_MINUTES;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    private final JwtKeyManager keyManager;
    private final JwtTokenBlackListService blacklistService;

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(now))
                .issuer("monio-api")
                .expiration(Date.from(now.plus(JWT_EXPIRATION_MINUTES, ChronoUnit.MINUTES)))
                .header().add("kid", keyManager.getCurrentKeyAlias())
                .and()
                .signWith(keyManager.getCurrentKey())
                .compact();
    }

    public Authentication validateToken(String token) throws SecurityException, SessionTerminatedException {
        if (blacklistService.isTokenBlackListed(token)) {
            throw new SessionTerminatedException("Session has been terminated by the application");
        }
        try {
            Claims claims = getTokenClaim(token);
            String userName = claims.getSubject();
            return new UsernamePasswordAuthenticationToken(userName, null, null);
        } catch (JwtException e) {
            throw new SecurityException("Invalid JWT token", e);
        }
    }

    public void invalidateToken(String token) {
        try {
            Claims claims = getTokenClaim(token);
            Date expiration = claims.getExpiration();
            if (expiration != null) {
                Duration timeToLive = Duration.between(Instant.now(), expiration.toInstant());
                if (!timeToLive.isNegative()) {
                    blacklistService.blackListToken(token, timeToLive);
                }
            }
        } catch (JwtException e) {
            throw new JwtException("Unable to invalidate JWT token: " + token + " due to: " + e.getMessage(), e);
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
