package com.smart_tiger.monio.middleware.security.verification;

import com.smart_tiger.monio.middleware.security.Encoder;
import com.smart_tiger.monio.modules.user.entity.UserAccount;
import com.smart_tiger.monio.modules.user.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppSecurityDetailService implements UserDetailsService {

    private final UserAccountRepository userRepo;
    private final Encoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount result = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new AppSecurityDetail(result);
    }

}
