package com.smart_tiger.monio.middleware.security;

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
        return List.of(new SimpleGrantedAuthority(AppSecurityRole.ROLE_USER.name()));
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
        return userAccount.isEnabled() && null != userAccount.getExpiryDate();
    }

    @Override
    public boolean isAccountNonLocked() {
        return userAccount.isLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !userAccount.isCredentialsExpired();
    }

    @Override
    public boolean isEnabled() {
        return userAccount.isEnabled();
    }

}
