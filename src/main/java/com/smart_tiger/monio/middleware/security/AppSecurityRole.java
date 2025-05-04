package com.smart_tiger.monio.middleware.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AppSecurityRole {
    ROLE_USER("ROLE_USER"),
    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_SUPER_ADMIN("ROLE_SUPER_ADMIN");

    @Getter
    private final String name;

    public static AppSecurityRole fromString(String name) {
        for (AppSecurityRole role : AppSecurityRole.values()) {
            if (role.getName().equals(name)) {
                return role;
            }
        }
        throw new IllegalArgumentException("No matching role for " + name);
    }

    @Override
    public String toString() {
        return name;
    }

}
