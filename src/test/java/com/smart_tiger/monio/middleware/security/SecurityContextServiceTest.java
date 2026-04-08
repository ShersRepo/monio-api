package com.smart_tiger.monio.middleware.security;

import com.smart_tiger.monio.middleware.exception.NotAuthorisedException;
import com.smart_tiger.monio.modules.user.UserAccountRepository;
import com.smart_tiger.monio.modules.user.entity.UserAccount;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@Tag("UnitTest")
@ExtendWith(MockitoExtension.class)
class SecurityContextServiceTest {

    @Mock
    private UserAccountRepository userRepo;

    @InjectMocks
    private SecurityContextService service;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUserId_shouldReturnUserIdFromSecurityContext() throws Exception {
        UUID userId = UUID.randomUUID();
        UserAccount account = new UserAccount();
        account.setId(userId);
        account.setUsername("tester");
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken("tester", "secret"));
        when(userRepo.findByUsername("tester")).thenReturn(Optional.of(account));

        UUID result = service.getCurrentUserId();

        assertThat(result).isEqualTo(userId);
    }

    @Test
    void getCurrentUserId_shouldThrowWhenAuthenticationMissing() {
        assertThrows(NotAuthorisedException.class, () -> service.getCurrentUserId());
    }

    @Test
    void getCurrentUserId_shouldThrowWhenPrincipalBlank() {
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(" ", "secret"));

        assertThrows(NotAuthorisedException.class, () -> service.getCurrentUserId());
    }

    @Test
    void getCurrentUserId_shouldThrowWhenUserCannotBeResolved() {
        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken("tester", "secret"));
        when(userRepo.findByUsername("tester")).thenReturn(Optional.empty());

        assertThrows(NotAuthorisedException.class, () -> service.getCurrentUserId());
    }
}
