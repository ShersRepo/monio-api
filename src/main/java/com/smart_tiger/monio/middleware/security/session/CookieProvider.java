package com.smart_tiger.monio.middleware.security.session;

import com.smart_tiger.monio.middleware.security.SecurityConstants;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

import static com.smart_tiger.monio.middleware.security.SecurityConstants.JWT_EXPIRATION_MINUTES;

@Component
public class CookieProvider {
    public void addJwtCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from(SecurityConstants.COOKIE_JWT_NAME, token)
                .httpOnly(true)
                .maxAge(Duration.ofDays(JWT_EXPIRATION_MINUTES))
                .secure(true)
                .path("/")
                // .domain() - Remove domain setting
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    public void clearJwtCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(SecurityConstants.COOKIE_JWT_NAME, "")
                .httpOnly(true)
                .maxAge(0)
                .secure(true)
                .path("/")
                // .domain() - Remove domain setting
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

}