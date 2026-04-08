package com.smart_tiger.monio.middleware.security.session;

import com.smart_tiger.monio.middleware.exception.JwtTokenBlackListFailureException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("UnitTest")
@ExtendWith(MockitoExtension.class)
class JwtTokenBlackListServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Test
    void blackListToken_shouldStoreTokenWhenAbsent() {
        JwtTokenBlackListService service = new JwtTokenBlackListService(redisTemplate);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent("token:blacklist:token", "1", Duration.ofMinutes(5))).thenReturn(true);

        service.blackListToken("token", Duration.ofMinutes(5));

        verify(valueOperations).setIfAbsent("token:blacklist:token", "1", Duration.ofMinutes(5));
    }

    @Test
    void blackListToken_shouldIgnoreAlreadyBlacklistedToken() {
        JwtTokenBlackListService service = new JwtTokenBlackListService(redisTemplate);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent("token:blacklist:token", "1", Duration.ofMinutes(5))).thenReturn(false);

        service.blackListToken("token", Duration.ofMinutes(5));
    }

    @Test
    void blackListToken_shouldWrapStorageFailures() {
        JwtTokenBlackListService service = new JwtTokenBlackListService(redisTemplate);
        when(redisTemplate.opsForValue()).thenThrow(new RuntimeException("redis unavailable"));

        assertThrows(JwtTokenBlackListFailureException.class,
                () -> service.blackListToken("token", Duration.ofMinutes(5)));
    }

    @Test
    void isTokenBlackListed_shouldReturnTrueWhenKeyExists() {
        JwtTokenBlackListService service = new JwtTokenBlackListService(redisTemplate);
        when(redisTemplate.hasKey("token:blacklist:token")).thenReturn(true);

        assertThat(service.isTokenBlackListed("token")).isTrue();
    }

    @Test
    void isTokenBlackListed_shouldReturnFalseWhenKeyMissing() {
        JwtTokenBlackListService service = new JwtTokenBlackListService(redisTemplate);
        when(redisTemplate.hasKey("token:blacklist:token")).thenReturn(false);

        assertThat(service.isTokenBlackListed("token")).isFalse();
    }

    @Test
    void isTokenBlackListed_shouldFailSecureOnErrors() {
        JwtTokenBlackListService service = new JwtTokenBlackListService(redisTemplate);
        when(redisTemplate.hasKey("token:blacklist:token")).thenThrow(new RuntimeException("redis unavailable"));

        assertThat(service.isTokenBlackListed("token")).isTrue();
    }

    @Test
    void removeFromBlacklist_shouldReturnTrueWhenDeleted() {
        JwtTokenBlackListService service = new JwtTokenBlackListService(redisTemplate);
        when(redisTemplate.delete("token:blacklist:token")).thenReturn(true);

        assertThat(service.removeFromBlacklist("token")).isTrue();
    }

    @Test
    void removeFromBlacklist_shouldReturnFalseOnErrors() {
        JwtTokenBlackListService service = new JwtTokenBlackListService(redisTemplate);
        when(redisTemplate.delete("token:blacklist:token")).thenThrow(new RuntimeException("redis unavailable"));

        assertThat(service.removeFromBlacklist("token")).isFalse();
    }

    @Test
    void getTokenTTL_shouldReturnDurationWhenPositive() {
        JwtTokenBlackListService service = new JwtTokenBlackListService(redisTemplate);
        when(redisTemplate.getExpire("token:blacklist:token", TimeUnit.SECONDS)).thenReturn(12L);

        assertThat(service.getTokenTTL("token")).isEqualTo(Duration.ofSeconds(12));
    }

    @Test
    void getTokenTTL_shouldReturnNullWhenNotBlacklistedOrOnErrors() {
        JwtTokenBlackListService service = new JwtTokenBlackListService(redisTemplate);
        when(redisTemplate.getExpire("token:blacklist:token", TimeUnit.SECONDS)).thenReturn(0L);
        assertThat(service.getTokenTTL("token")).isNull();

        when(redisTemplate.getExpire("token:blacklist:error", TimeUnit.SECONDS)).thenThrow(new RuntimeException("redis unavailable"));
        assertThat(service.getTokenTTL("error")).isNull();
    }
}
