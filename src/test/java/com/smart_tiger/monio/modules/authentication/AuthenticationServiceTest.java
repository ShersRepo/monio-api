package com.smart_tiger.monio.modules.authentication;

import com.smart_tiger.monio.middleware.response.ApiResponse;
import com.smart_tiger.monio.middleware.security.session.CookieProvider;
import com.smart_tiger.monio.middleware.security.session.JwtTokenProvider;
import com.smart_tiger.monio.modules.user.UserAccountService;
import com.smart_tiger.monio.modules.user.dto.UserAccountDto;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static com.smart_tiger.monio.middleware.security.SecurityConstants.COOKIE_JWT_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@Tag("UnitTest")
@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private CookieProvider cookieProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private UserAccountService userAccountService;

    @InjectMocks
    private AuthenticationService authenticationService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void authenticateRequestUser_shouldAuthenticateAndSetCookie() {
        AuthenticationDetailsDto dto = new AuthenticationDetailsDto("tester", "secret");
        MockHttpServletResponse response = new MockHttpServletResponse();
        Authentication authentication = new UsernamePasswordAuthenticationToken("tester", "secret");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(tokenProvider.generateToken(authentication)).thenReturn("jwt-token");

        ApiResponse<Void> result = authenticationService.authenticateRequestUser(response, dto);

        assertThat(result.getMessage()).isEqualTo("Authorization successful");
        assertThat(result.getStatus()).isEqualTo(204);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenProvider).generateToken(authentication);
        verify(cookieProvider).addJwtCookie(response, "jwt-token");
    }

    @Test
    void authenticateRequestUser_shouldPropagateAuthenticationException() {
        AuthenticationDetailsDto dto = new AuthenticationDetailsDto("tester", "wrong");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("bad credentials"));

        assertThrows(BadCredentialsException.class, () -> authenticationService.authenticateRequestUser(response, dto));

        verify(tokenProvider, never()).generateToken(any());
        verify(cookieProvider, never()).addJwtCookie(any(), anyString());
    }

    @Test
    void logoutUser_shouldClearCookieInvalidateTokenAndClearSecurityContext() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setCookies(new Cookie(COOKIE_JWT_NAME, "jwt-token"));
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("tester", "secret"));

        ApiResponse<Void> result = authenticationService.logoutUser(request, response);

        assertThat(result.getMessage()).isEqualTo("Logged out successfully");
        assertThat(result.getStatus()).isEqualTo(204);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(cookieProvider).clearJwtCookie(response);
        verify(tokenProvider).invalidateToken("jwt-token");
    }

    @Test
    void logoutUser_shouldSkipInvalidationWhenTokenCookieMissing() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        ApiResponse<Void> result = authenticationService.logoutUser(request, response);

        assertThat(result.getMessage()).isEqualTo("Logged out successfully");
        verify(cookieProvider).clearJwtCookie(response);
        verify(tokenProvider, never()).invalidateToken(anyString());
    }

    @Test
    void refreshUser_shouldReturnFetchedUser() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie(COOKIE_JWT_NAME, "jwt-token"));
        UserAccountDto user = new UserAccountDto();
        UUID userId = UUID.randomUUID();
        user.setId(userId);
        user.setUsername("tester");

        when(tokenProvider.getUsernameFromToken("jwt-token")).thenReturn("tester");
        when(userAccountService.getUserAccount("tester")).thenReturn(user);

        ApiResponse<UserAccountDto> result = authenticationService.refreshUser(request);

        assertThat(result.getStatus()).isEqualTo(200);
        assertThat(result.getMessage()).isEqualTo("Fetched data");
        assertThat(result.getData()).isEqualTo(user);
        verify(tokenProvider).getUsernameFromToken("jwt-token");
        verify(userAccountService).getUserAccount("tester");
    }

    @Test
    void refreshUser_shouldThrowWhenTokenCookieMissing() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        IllegalStateException error = assertThrows(IllegalStateException.class, () -> authenticationService.refreshUser(request));

        assertThat(error.getMessage()).isEqualTo("No JWT token found");
        verify(tokenProvider, never()).getUsernameFromToken(anyString());
        verify(userAccountService, never()).getUserAccount(anyString());
    }
}
