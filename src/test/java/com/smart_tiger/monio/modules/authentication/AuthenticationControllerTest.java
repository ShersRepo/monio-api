package com.smart_tiger.monio.modules.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smart_tiger.monio.middleware.response.ApiResponse;
import com.smart_tiger.monio.modules.user.dto.UserAccountDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("UnitTest")
@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void login_shouldReturnOkWhenAuthenticationSucceeds() throws Exception {
        AuthenticationDetailsDto dto = new AuthenticationDetailsDto("tester", "secret");
        ApiResponse<Void> response = new ApiResponse<>(204, "Authorization successful", Collections.emptyList(), null);

        when(authenticationService.authenticateRequestUser(any(), any(AuthenticationDetailsDto.class))).thenReturn(response);

        mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(204))
                .andExpect(jsonPath("$.message").value("Authorization successful"));
    }

    @Test
    void login_shouldReturnUnauthorizedWhenAuthenticationFails() throws Exception {
        AuthenticationDetailsDto dto = new AuthenticationDetailsDto("tester", "wrong");

        when(authenticationService.authenticateRequestUser(any(), any(AuthenticationDetailsDto.class)))
                .thenThrow(new BadCredentialsException("bad credentials"));

        mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    @Test
    void login_shouldRejectInvalidPayload() throws Exception {
        AuthenticationDetailsDto dto = new AuthenticationDetailsDto("", "");

        mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void logout_shouldReturnOkResponse() throws Exception {
        ApiResponse<Void> response = new ApiResponse<>(204, "Logged out successfully", Collections.emptyList(), null);

        when(authenticationService.logoutUser(any(), any())).thenReturn(response);

        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(204))
                .andExpect(jsonPath("$.message").value("Logged out successfully"));
    }

    @Test
    void refreshUser_shouldReturnCurrentUserStatus() throws Exception {
        UserAccountDto user = new UserAccountDto();
        UUID userId = UUID.randomUUID();
        user.setId(userId);
        user.setUsername("tester");
        user.setFirstName("Test");
        ApiResponse<UserAccountDto> response = new ApiResponse<>(200, "Fetched data", Collections.emptyList(), user);

        when(authenticationService.refreshUser(any())).thenReturn(response);

        mockMvc.perform(get("/auth/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.id").value(userId.toString()))
                .andExpect(jsonPath("$.data.username").value("tester"))
                .andExpect(jsonPath("$.data.firstName").value("Test"));
    }
}
