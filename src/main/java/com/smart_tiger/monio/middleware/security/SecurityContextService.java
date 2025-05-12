package com.smart_tiger.monio.middleware.security;

import com.smart_tiger.monio.middleware.exception.NotAuthorisedException;
import com.smart_tiger.monio.modules.user.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.smart_tiger.monio.utils.StringUtils.isNotNullOrEmpty;

@Service
@RequiredArgsConstructor
public class SecurityContextService {

    private final UserAccountRepository userRepo;

    public UUID getCurrentUserId() throws NotAuthorisedException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && isNotNullOrEmpty(authentication.getPrincipal().toString())) {
            return userRepo.findByUsername(String.valueOf(authentication.getPrincipal()))
                    .orElseThrow(NotAuthorisedException::new)
                    .getId();
        } else {
            throw new NotAuthorisedException();
        }
    }


}
