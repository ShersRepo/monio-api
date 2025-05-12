package com.smart_tiger.monio.modules.authentication;

import com.smart_tiger.monio.middleware.response.ApiResponse;
import com.smart_tiger.monio.middleware.response.SuccessResponseType;
import com.smart_tiger.monio.middleware.security.session.CookieProvider;
import com.smart_tiger.monio.middleware.security.session.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import static com.smart_tiger.monio.middleware.configuration.JwtAuthenticationFilter.getTokenFromCookie;

@RequiredArgsConstructor
@Service
public class AuthenticationService {

    private final CookieProvider cookieProvider;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public ApiResponse<Void> authenticateRequestUser(
            HttpServletResponse response,
            AuthenticationDetailsDto authenticationDetailsDto
    ) throws AuthenticationException {
        // Authenticate the user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationDetailsDto.getUsername(),
                        authenticationDetailsDto.getPassword()
                )
        );

        // Generate JWT token
        String jwt = tokenProvider.generateToken(authentication);

        // Create secure cookie
        cookieProvider.addJwtCookie(response, jwt);
        return SuccessResponseType.okWithoutDataResponse("Authorization successful");
    }

    public ApiResponse<Void> logoutUser(HttpServletRequest request, HttpServletResponse response) {
        // Clear the JWT cookie
        cookieProvider.clearJwtCookie(response);

        // Invalidate the JWT token
        getTokenFromCookie(request).ifPresent(tokenProvider::invalidateToken);

        // Clear the security context
        SecurityContextHolder.clearContext();

        return SuccessResponseType.okWithoutDataResponse("Logged out successfully");
    }


}
