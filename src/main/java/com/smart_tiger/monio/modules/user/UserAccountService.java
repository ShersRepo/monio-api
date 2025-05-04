package com.smart_tiger.monio.modules.user;

import com.smart_tiger.monio.middleware.exception.ResourceCouldNotCreated;
import com.smart_tiger.monio.middleware.exception.ResourceNotFoundException;
import com.smart_tiger.monio.middleware.security.Encoder;
import com.smart_tiger.monio.modules.user.dto.UserAccountCreateDto;
import com.smart_tiger.monio.modules.user.dto.UserAccountDto;
import com.smart_tiger.monio.modules.user.entity.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserAccountService {

    private final UserAccountRepository repo;
    private final Encoder encoder;
    private final UserAccountMapper mapper;

    @Transactional
    public UUID createUserAccount(UserAccountCreateDto dto) {
        dto.setPassword(encoder.passwordEncoder().encode(dto.getPassword()));
        UserAccount userAccount = mapper.createDtoToEntity(dto);
        userAccount.setEnabled(true);
        userAccount.setLocked(false);
        userAccount.setCredentialsExpired(false);
        UserAccount resultEntity = repo.save(userAccount);

        return repo.findById(resultEntity.getId())
                .orElseThrow(() -> new ResourceCouldNotCreated(dto.getUserName()))
                .getId();
    }

    public UserAccountDto getUserAccount(String userName) {
        return repo.findByUsername(userName)
                .map(mapper::entityToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
    }

}
