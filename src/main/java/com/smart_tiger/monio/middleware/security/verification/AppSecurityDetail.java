package com.smart_tiger.monio.middleware.security.verification;

import com.smart_tiger.monio.modules.user.constant.UserAccountStatus;
import com.smart_tiger.monio.modules.user.entity.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class AppSecurityDetail implements UserDetails {

    private final UserAccount userAccount;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(AppSecurityRole.APP_USER.name()));
    }

    @Override
    public String getPassword() {
        return userAccount.getPassword();
    }

    @Override
    public String getUsername() {
        return userAccount.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return !userAccount.getStatus().equals(UserAccountStatus.CLOSED);
    }

    @Override
    public boolean isAccountNonLocked() {
        return !userAccount.getStatus().equals(UserAccountStatus.LOCKED);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !userAccount.getStatus().equals(UserAccountStatus.CREDENTIAL_EXPIRED);
    }

    @Override
    public boolean isEnabled() {
        return !userAccount.getStatus().equals(UserAccountStatus.DISABLED);
    }

}
