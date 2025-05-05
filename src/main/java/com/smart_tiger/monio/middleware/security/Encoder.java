package com.smart_tiger.monio.middleware.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class Encoder {

    private final PasswordEncoder passwordEncoder;

    public Encoder() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return this.passwordEncoder;
    }

}
