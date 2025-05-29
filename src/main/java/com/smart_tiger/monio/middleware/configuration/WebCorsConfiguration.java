package com.smart_tiger.monio.middleware.configuration;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


public class WebCorsConfiguration {

    private WebCorsConfiguration() {}

    // Define CORS configuration source
    static UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:3001")); // Allow specific origin
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH")); // Allow HTTP methods
//        config.setAllowedHeaders(List.of("Content-Type", "Authorization")); // Allow specific headers
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true); // Allow cookies or credentials if needed

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // Apply CORS rules to all endpoints
        return source;
    }

}
