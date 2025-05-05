package com.smart_tiger.monio.middleware.security.authentication;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "jwt.keystore")
public class JwtKeyStoreConfig {
    private String storeLocation;
    private String password;
    private String keyAlias;
    private String keyPassword;
}
