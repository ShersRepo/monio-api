package com.smart_tiger.monio.middleware.security.session;

import com.smart_tiger.monio.middleware.security.SecurityConstants;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import static com.smart_tiger.monio.middleware.security.SecurityConstants.JWT_EXPIRATION_MINUTES;
import static org.springframework.boot.web.server.Cookie.SameSite.STRICT;

@Component
public class CookieProvider {

    private final String expiredSessionCookie = constructCookie("", 0).toString();

    public void addJwtCookie(HttpServletResponse response, String token) {
        response.addHeader("Set-Cookie", constructCookie(token, JWT_EXPIRATION_MINUTES).toString());
    }

    public void clearJwtCookie(HttpServletResponse response) {
        response.addHeader("Set-Cookie", expiredSessionCookie);
    }

    private ResponseCookie constructCookie(String token, long maxAgeSeconds) {
        return ResponseCookie.from(SecurityConstants.COOKIE_JWT_NAME, token)
                .httpOnly(true)
                .secure(true)
                .sameSite(STRICT.attributeValue())
                .maxAge(maxAgeSeconds)
                .path("/")
                .build();
    }

}