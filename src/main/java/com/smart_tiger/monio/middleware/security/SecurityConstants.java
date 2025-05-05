package com.smart_tiger.monio.middleware.security;

import org.springframework.beans.factory.annotation.Value;

public final class SecurityConstants {
    public static final String COOKIE_JWT_NAME = "AUTH_TOKEN";
    public static final long JWT_EXPIRATION_MINUTES = 24 * 60L; // 1 day
    @Value("${jwt.keystore.key-alias}")
    public static String JWT_KEY_ALIAS;

    private SecurityConstants() {} // Prevent instantiation

}