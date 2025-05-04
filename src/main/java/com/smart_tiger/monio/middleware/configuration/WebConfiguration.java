package com.smart_tiger.monio.middleware.configuration;

import com.smart_tiger.monio.middleware.security.AppSecurityDetailService;
import com.smart_tiger.monio.middleware.security.AppSecurityRole;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    private final AppSecurityDetailService userSecurityService;

    /**
     * Configuration for basic authentication requirement
     */
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User
                .withDefaultPasswordEncoder()
                .username("superadmin")
                .password("password")
                .roles(AppSecurityRole.ROLE_USER.toString())
                .build();

        return new InMemoryUserDetailsManager(user);
    }

    /**
     * Configuration for No Authentication and temporary testing
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // Allow all requests without authentication
                )
                .cors(c -> c.configurationSource(getCORSConfig()))
                .userDetailsService(userDetailsService())
                .formLogin(Customizer.withDefaults())
                .sessionManagement(sess -> sess
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .build();
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix("api", HandlerTypePredicate.forAnnotation(RestController.class));
    }

    // Define CORS configuration source
    private UrlBasedCorsConfigurationSource getCORSConfig() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000")); // Allow specific origin
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH")); // Allow HTTP methods
        config.setAllowedHeaders(List.of("Content-Type", "Authorization")); // Allow specific headers
        config.setAllowCredentials(true); // Allow cookies or credentials if needed

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // Apply CORS rules to all endpoints
        return source;
    }

}
