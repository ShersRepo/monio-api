package com.smart_tiger.monio.middleware.configuration;

import com.smart_tiger.monio.middleware.security.session.JwtTokenProvider;
import com.smart_tiger.monio.modules.user.UserAccountRepository;
import com.smart_tiger.monio.modules.user.entity.UserAccount;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String COOKIE_AUTH_NAME = "AUTH_TOKEN";
    private final UserAccountRepository userRepo;
    private final JwtTokenProvider tokenProvider;
    private final Set<String> exemptRequestURI;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain
    ) throws ServletException, IOException {
        Optional<String> cookieToken = getTokenFromCookie(request);
        if (cookieToken.isPresent() && tokenProvider.validateToken(cookieToken.get()).isAuthenticated()) {
            String username = tokenProvider.getUsernameFromToken(cookieToken.get());

            UserAccount result = userRepo.findByUsername(username).orElseThrow(() ->
                    new ServletException("Authentication failed due to credentials"));

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    result.getUsername(), result.getPassword(), result.getRoles());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            filterChain.doFilter(request, response);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return exemptRequestURI.contains(request.getRequestURI());
    }

    public static Optional<String> getTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (COOKIE_AUTH_NAME.equals(cookie.getName())) {
                    return Optional.of(cookie.getValue());
                }
            }
        }
        return Optional.empty();
    }

}
