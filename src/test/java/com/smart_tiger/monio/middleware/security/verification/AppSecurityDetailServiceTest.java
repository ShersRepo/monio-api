package com.smart_tiger.monio.middleware.security.verification;

import com.smart_tiger.monio.middleware.security.Encoder;
import com.smart_tiger.monio.modules.user.UserAccountRepository;
import com.smart_tiger.monio.modules.user.constant.UserAccountStatus;
import com.smart_tiger.monio.modules.user.entity.UserAccount;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@Tag("UnitTest")
@ExtendWith(MockitoExtension.class)
class AppSecurityDetailServiceTest {

    @Mock
    private UserAccountRepository userRepo;

    @Mock
    private Encoder encoder;

    @InjectMocks
    private AppSecurityDetailService service;

    @Test
    void loadUserByUsername_shouldReturnUserDetailsWhenUserExists() {
        UserAccount user = new UserAccount();
        user.setUsername("tester");
        user.setPassword("encoded-password");
        user.setStatus(UserAccountStatus.ACTIVE);

        when(userRepo.findByUsername("tester")).thenReturn(Optional.of(user));

        UserDetails result = service.loadUserByUsername("tester");

        assertThat(result).isInstanceOf(AppSecurityDetail.class);
        assertThat(result.getUsername()).isEqualTo("tester");
        assertThat(result.getPassword()).isEqualTo("encoded-password");
        assertThat(result.isEnabled()).isTrue();
    }

    @Test
    void loadUserByUsername_shouldThrowWhenUserMissing() {
        when(userRepo.findByUsername("tester")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("tester"));
    }
}
