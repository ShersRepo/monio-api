package com.smart_tiger.monio.modules.user.login;

import com.smart_tiger.monio.middleware.response.ApiResponse;
import com.smart_tiger.monio.middleware.response.SuccessResponseType;
import com.smart_tiger.monio.middleware.security.authentication.CookieProvider;
import com.smart_tiger.monio.middleware.security.authentication.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthenticationService {

    private final CookieProvider cookieProvider;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public ApiResponse<Void> authenticateRequestUser(
            HttpServletResponse response,
            AuthenticationDto authenticationDto
    ) throws AuthenticationException {
        // Authenticate the user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationDto.getUsername(),
                        authenticationDto.getPassword()
                )
        );

        // Generate JWT token
        String jwt = tokenProvider.generateToken(authentication);

        // Create secure cookie
        cookieProvider.addJwtCookie(response, jwt);
        return SuccessResponseType.okWithoutDataResponse("Authorization successful");
    }

    public ApiResponse<Void> logoutUser(HttpServletResponse response) {
        // Clear the JWT cookie
        cookieProvider.clearJwtCookie(response);

        // Clear the security context
        SecurityContextHolder.clearContext();

        return SuccessResponseType.okWithoutDataResponse("Logged out successfully");
    }


}
