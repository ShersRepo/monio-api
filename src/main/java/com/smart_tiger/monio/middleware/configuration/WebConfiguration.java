package com.smart_tiger.monio.middleware.configuration;

import com.smart_tiger.monio.middleware.security.verification.AppSecurityDetailService;
import com.smart_tiger.monio.middleware.security.Encoder;
import com.smart_tiger.monio.middleware.security.session.JwtTokenProvider;
import com.smart_tiger.monio.modules.user.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Set;

import static com.smart_tiger.monio.middleware.configuration.WebCorsConfiguration.corsConfigurationSource;

@RequiredArgsConstructor
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    private final AppSecurityDetailService userSecurityService;
    private final JwtTokenProvider tokenProvider;
    private final Encoder encoder;
    private final UserAccountRepository userAccountRepository;
    private static final Set<String> exemptJwtPOSTEndpoints = Set.of(
            "/api/auth/login",
            "/api/auth/logout",
            "/api/user/create"
    );

    /**
     * Configuration for No Authentication and temporary testing
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)  // Deny frame embedding and prevents clickjacking attacks
                        .contentTypeOptions(Customizer.withDefaults()) // Enables and Prevents MIME-type sniffing
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives("default-src 'self'; " +
                                        "script-src 'self'; " +
                                        "style-src 'self'; " +
                                        "img-src 'self'; " +
                                        "frame-src 'none';"
                                ))
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                HttpMethod.POST,
                                exemptJwtPOSTEndpoints.toArray(new String[0])
                        )
                        .permitAll()
                        .anyRequest().authenticated() // Allow all requests without authentication
                )
                .exceptionHandling(exc -> exc
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .cors(c -> c.configurationSource(corsConfigurationSource()))
                .userDetailsService(userSecurityService)
                .sessionManagement(sess -> sess
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(new JwtAuthenticationFilter(userAccountRepository, tokenProvider, exemptJwtPOSTEndpoints), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix("api", HandlerTypePredicate.forAnnotation(RestController.class));
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userSecurityService);
        authProvider.setPasswordEncoder(encoder.passwordEncoder());
        return new ProviderManager(authProvider);
    }

}
