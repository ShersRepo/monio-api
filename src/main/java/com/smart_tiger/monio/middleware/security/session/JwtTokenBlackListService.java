package com.smart_tiger.monio.middleware.security.session;

import com.smart_tiger.monio.middleware.exception.JwtTokenBlackListFailureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static java.lang.Boolean.TRUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenBlackListService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String BLACKLIST_PREFIX = "token:blacklist:";
    private static final String BLACKLIST_VALUE = "1";

    /**
     * Blacklists a given JWT token for a specified duration.
     * The token is stored in Redis with a time-to-live (TTL) specified by the `timeToLive` parameter.
     * If the token is already blacklisted, a warning is logged.
     *
     * @param token the JWT token to be blacklisted
     * @param timeToLive the duration for which the token remains blacklisted
     * @throws JwtTokenBlackListFailureException if there is a failure during the blacklisting process
     */
    public void blackListToken(String token, Duration timeToLive) {
        try {
            String key = BLACKLIST_PREFIX + token;
            ValueOperations<String, String> ops = redisTemplate.opsForValue();

            boolean wasBlackListed = TRUE.equals(ops.setIfAbsent(key, BLACKLIST_VALUE, timeToLive));

            if (wasBlackListed) {
                log.debug("Token blacklisted successfully with TTL: {} seconds", timeToLive.getSeconds());
            } else {
                log.warn("Token was already blacklisted: {}", key);
            }
        } catch (Exception e) {
            log.error("Failed to blacklist token", e);
            throw new JwtTokenBlackListFailureException("Failed to blacklist token", e);
        }
    }

    /**
     * Checks if a given JWT token is blacklisted.
     *
     * @param token the JWT token to check for blacklist status
     * @return true if the token is blacklisted or if an error occurs during the check,
     *         false otherwise
     */
    public boolean isTokenBlackListed(String token) {
        try {
            String key = BLACKLIST_PREFIX + token;
            Boolean hasKey = redisTemplate.hasKey(key);
            return TRUE.equals(hasKey);
        } catch (Exception e) {
            log.error("Failed to check token blacklist status", e);
            // Fail secure - if we can't check the blacklist, assume the token is invalid
            return true;
        }

    }

    /**
     * Removes a token from the blacklist
     *
     * @param token JWT token to remove from blacklist
     * @return true if token was removed, false if it didn't exist
     */
    public boolean removeFromBlacklist(String token) {
        try {
            String key = BLACKLIST_PREFIX + token;
            return Boolean.TRUE.equals(redisTemplate.delete(key));
        } catch (Exception e) {
            log.error("Failed to remove token from blacklist", e);
            return false;
        }
    }

    /**
     * Gets the remaining time to live for a blacklisted token
     *
     * @param token JWT token
     * @return Duration representing remaining TTL, null if token is not blacklisted
     */
    public Duration getTokenTTL(String token) {
        try {
            String key = BLACKLIST_PREFIX + token;
            Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            return ttl != null && ttl > 0 ? Duration.ofSeconds(ttl) : null;
        } catch (Exception e) {
            log.error("Failed to get token TTL", e);
            return null;
        }
    }

}
