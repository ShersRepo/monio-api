package com.smart_tiger.monio.modules.user;

import com.smart_tiger.monio.middleware.exception.ResourceAlreadyExistsException;
import com.smart_tiger.monio.middleware.exception.ResourceCouldNotCreated;
import com.smart_tiger.monio.middleware.exception.ResourceNotFoundException;
import com.smart_tiger.monio.middleware.security.Encoder;
import com.smart_tiger.monio.modules.user.constant.UserAccountStatus;
import com.smart_tiger.monio.modules.user.dto.UserAccountCreateDto;
import com.smart_tiger.monio.modules.user.dto.UserAccountDto;
import com.smart_tiger.monio.modules.user.entity.UserAccount;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("UnitTest")
@ExtendWith(MockitoExtension.class)
class UserAccountServiceTest {

    @Mock
    private UserAccountRepository repo;

    @Mock
    private Encoder encoder;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserAccountMapper mapper;

    @InjectMocks
    private UserAccountService service;

    @Test
    void createUserAccount_shouldThrowWhenUsernameAlreadyExists() {
        UserAccountCreateDto dto = buildCreateDto();
        when(repo.findByUsername("tester")).thenReturn(Optional.of(new UserAccount()));

        assertThrows(ResourceAlreadyExistsException.class, () -> service.createUserAccount(dto));

        verify(encoder, never()).passwordEncoder();
        verify(mapper, never()).createDtoToEntity(any());
        verify(repo, never()).save(any());
    }

    @Test
    void createUserAccount_shouldEncodeAndPersistUser() {
        UserAccountCreateDto dto = buildCreateDto();
        UUID createdId = UUID.randomUUID();
        UserAccount mapped = new UserAccount();
        UserAccount saved = new UserAccount();
        saved.setId(createdId);

        when(repo.findByUsername("tester")).thenReturn(Optional.empty());
        when(encoder.passwordEncoder()).thenReturn(passwordEncoder);
        when(passwordEncoder.encode("plain-password")).thenReturn("encoded-password");
        when(mapper.createDtoToEntity(dto)).thenReturn(mapped);
        when(repo.save(mapped)).thenReturn(saved);
        when(repo.findById(createdId)).thenReturn(Optional.of(saved));

        UUID result = service.createUserAccount(dto);

        ArgumentCaptor<UserAccount> captor = ArgumentCaptor.forClass(UserAccount.class);
        verify(repo).save(captor.capture());
        UserAccount entity = captor.getValue();

        assertThat(result).isEqualTo(createdId);
        assertThat(dto.getPassword()).isEqualTo("encoded-password");
        assertThat(entity.isCredentialsExpired()).isFalse();
        assertThat(entity.getStatus()).isEqualTo(UserAccountStatus.VERIFICATION_PENDING);
        assertThat(entity.getVerificationPendingStartDate()).isNotNull();
        assertThat(entity.getCreatedDate()).isNotNull();
    }

    @Test
    void createUserAccount_shouldThrowWhenCreatedUserCannotBeReloaded() {
        UserAccountCreateDto dto = buildCreateDto();
        UUID createdId = UUID.randomUUID();
        UserAccount mapped = new UserAccount();
        UserAccount saved = new UserAccount();
        saved.setId(createdId);

        when(repo.findByUsername("tester")).thenReturn(Optional.empty());
        when(encoder.passwordEncoder()).thenReturn(passwordEncoder);
        when(passwordEncoder.encode("plain-password")).thenReturn("encoded-password");
        when(mapper.createDtoToEntity(dto)).thenReturn(mapped);
        when(repo.save(mapped)).thenReturn(saved);
        when(repo.findById(createdId)).thenReturn(Optional.empty());

        assertThrows(ResourceCouldNotCreated.class, () -> service.createUserAccount(dto));
    }

    @Test
    void getUserAccount_shouldReturnMappedDto() {
        UserAccount entity = new UserAccount();
        entity.setUsername("tester");
        UserAccountDto dto = new UserAccountDto();
        dto.setUsername("tester");

        when(repo.findByUsername("tester")).thenReturn(Optional.of(entity));
        when(mapper.entityToDto(entity)).thenReturn(dto);

        UserAccountDto result = service.getUserAccount("tester");

        assertThat(result).isEqualTo(dto);
    }

    @Test
    void getUserAccount_shouldThrowWhenMissing() {
        when(repo.findByUsername("tester")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getUserAccount("tester"));
        verify(mapper, never()).entityToDto(any());
    }

    private static UserAccountCreateDto buildCreateDto() {
        UserAccountCreateDto dto = new UserAccountCreateDto();
        dto.setUsername("tester");
        dto.setFirstName("Test");
        dto.setLastName("User");
        dto.setEmail("test@example.com");
        dto.setPassword("plain-password");
        return dto;
    }
}
