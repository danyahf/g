package com.danya.auth;

import com.danya.auth.api.AuthController;
import com.danya.exception.BadCredentialsException;
import com.danya.exception.GlobalExceptionHandler;
import com.danya.user.dto.CredentialsDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AuthControllerTest {
    private final AuthService authService = Mockito.mock(AuthService.class);
    private final AuthController authController = new AuthController(authService);
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void loginReturns200AndTokenOnSuccess() throws Exception {
        when(authService.authenticate(any(CredentialsDto.class)))
                .thenReturn(new TokenDto("access-token-123"));

        CredentialsDto body = new CredentialsDto("john.doe", "pass123");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").value("access-token-123"));

        verify(authService).authenticate(any(CredentialsDto.class));
        verifyNoMoreInteractions(authService);
    }

    @Test
    void loginReturns401OnBadCredentials() throws Exception {
        doThrow(new BadCredentialsException())
                .when(authService).authenticate(any(CredentialsDto.class));

        var body = new CredentialsDto("ghost", "wrong");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnauthorized());

        verify(authService).authenticate(any(CredentialsDto.class));
        verifyNoMoreInteractions(authService);
    }
}
