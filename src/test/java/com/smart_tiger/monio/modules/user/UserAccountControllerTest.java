package com.smart_tiger.monio.modules.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smart_tiger.monio.middleware.exception.GlobalExceptionHandler;
import com.smart_tiger.monio.modules.user.dto.UserAccountCreateDto;
import com.smart_tiger.monio.modules.user.dto.UserAccountDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("UnitTest")
@ExtendWith(MockitoExtension.class)
class UserAccountControllerTest {

    @Mock
    private UserAccountService userAccountService;

    @InjectMocks
    private UserAccountController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void createUserAccount_shouldReturnCreatedResponse() throws Exception {
        UserAccountCreateDto dto = validCreateDto();
        UUID createdId = UUID.randomUUID();

        when(userAccountService.createUserAccount(dto)).thenReturn(createdId);

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "user/" + createdId))
                .andExpect(jsonPath("$.status").value(204))
                .andExpect(jsonPath("$.message").value("User Account setup"));
    }

    @Test
    void createUserAccount_shouldRejectInvalidPayload() throws Exception {
        UserAccountCreateDto dto = new UserAccountCreateDto();
        dto.setUsername("");

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void getUserAccount_shouldReturnUser() throws Exception {
        UUID userId = UUID.randomUUID();
        UserAccountDto dto = new UserAccountDto();
        dto.setId(userId);
        dto.setUsername("tester");
        dto.setEmail("test@example.com");

        when(userAccountService.getUserAccount("tester")).thenReturn(dto);

        mockMvc.perform(get("/user/tester"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Fetched data"))
                .andExpect(jsonPath("$.data.id").value(userId.toString()))
                .andExpect(jsonPath("$.data.username").value("tester"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));
    }

    private static UserAccountCreateDto validCreateDto() {
        UserAccountCreateDto dto = new UserAccountCreateDto();
        dto.setUsername("tester");
        dto.setFirstName("Test");
        dto.setLastName("User");
        dto.setEmail("test@example.com");
        dto.setPassword("plain-password");
        return dto;
    }
}
