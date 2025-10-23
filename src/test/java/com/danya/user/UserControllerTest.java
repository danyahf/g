package com.danya.user;

import com.danya.exception.EntityNotFoundException;
import com.danya.exception.GlobalExceptionHandler;
import com.danya.exception.InvalidCurrentPasswordException;
import com.danya.security.authentication.AuthUserArgumentResolver;
import com.danya.security.authorization.RoleAuthorizationInterceptor;
import com.danya.user.api.UserController;
import com.danya.user.dto.PasswordChangeDto;
import com.danya.user.dto.ProfileStatusChangeDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static com.danya.AuthUserRequestPostProcessors.authUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest {

    private final UserService userService = Mockito.mock(UserService.class);
    private final UserController userController = new UserController(userService);
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        var validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .setCustomArgumentResolvers(new AuthUserArgumentResolver())
                .addMappedInterceptors(new String[]{"/**"}, new RoleAuthorizationInterceptor())
                .build();
    }

    @Test
    void changePasswordReturns204ForValidPayload() throws Exception {
        PasswordChangeDto body = new PasswordChangeDto("oldPass123", "newPass456");

        mockMvc.perform(put("/users/{username}/password", "john.doe")
                        .with(authUser("admin.admin", List.of("ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNoContent());

        verify(userService).changePassword(eq("john.doe"), any(PasswordChangeDto.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    void meChangePasswordReturns204ForValidPayload() throws Exception {
        PasswordChangeDto body = new PasswordChangeDto("oldPass123", "newPass456");

        mockMvc.perform(put("/users/me/password")
                        .with(authUser("john.doe", List.of("TRAINEE")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNoContent());

        verify(userService).changePassword(eq("john.doe"), any(PasswordChangeDto.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    void changePasswordReturns404WhenEntityNotFound() throws Exception {
        doThrow(new EntityNotFoundException("User profile not found"))
                .when(userService).changePassword(eq("ghost"), any(PasswordChangeDto.class));

        PasswordChangeDto body = new PasswordChangeDto("old", "newStrong");

        mockMvc.perform(put("/users/{username}/password", "ghost")
                        .with(authUser("admin.admin", List.of("ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNotFound());

        verify(userService).changePassword(eq("ghost"), any(PasswordChangeDto.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    void meChangePasswordReturns404WhenEntityNotFound() throws Exception {
        doThrow(new EntityNotFoundException("User profile not found"))
                .when(userService).changePassword(eq("ghost.ghost"), any(PasswordChangeDto.class));

        PasswordChangeDto body = new PasswordChangeDto("old", "newStrong");

        mockMvc.perform(put("/users/me/password")
                        .with(authUser("ghost.ghost", List.of("TRAINEE")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNotFound());

        verify(userService).changePassword(eq("ghost.ghost"), any(PasswordChangeDto.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    void changePasswordReturns400WhenCurrentPasswordInvalid() throws Exception {
        doThrow(new InvalidCurrentPasswordException())
                .when(userService).changePassword(eq("john.doe"), any(PasswordChangeDto.class));

        PasswordChangeDto body = new PasswordChangeDto("wrongOld", "newStrong");

        mockMvc.perform(put("/users/{username}/password", "john.doe")
                        .with(authUser("admin.admin", List.of("ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());

        verify(userService).changePassword(eq("john.doe"), any(PasswordChangeDto.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    void meChangePasswordReturns400WhenCurrentPasswordInvalid() throws Exception {
        doThrow(new InvalidCurrentPasswordException())
                .when(userService).changePassword(eq("john.doe"), any(PasswordChangeDto.class));

        PasswordChangeDto body = new PasswordChangeDto("wrongOld", "newStrong");

        mockMvc.perform(put("/users/me/password")
                        .with(authUser("john.doe", List.of("TRAINEE")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());

        verify(userService).changePassword(eq("john.doe"), any(PasswordChangeDto.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    void changeStatusReturns204ForValidPayload() throws Exception {
        ProfileStatusChangeDto body = new ProfileStatusChangeDto(ActivationStatus.ACTIVE);

        mockMvc.perform(put("/users/{username}/status", "john.doe")
                        .with(authUser("admin.admin", List.of("ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNoContent());

        verify(userService).changeStatus(eq("john.doe"), any(ProfileStatusChangeDto.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    void meChangeStatusReturns204ForValidPayload() throws Exception {
        ProfileStatusChangeDto body = new ProfileStatusChangeDto(ActivationStatus.ACTIVE);

        mockMvc.perform(put("/users/me/status")
                        .with(authUser("john.doe", List.of("TRAINEE")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNoContent());

        verify(userService).changeStatus(eq("john.doe"), any(ProfileStatusChangeDto.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    void changeStatusReturns404WhenEntityNotFound() throws Exception {
        doThrow(new EntityNotFoundException("User profile not found"))
                .when(userService).changeStatus(eq("ghost"), any(ProfileStatusChangeDto.class));

        ProfileStatusChangeDto body = new ProfileStatusChangeDto(ActivationStatus.ACTIVE);

        mockMvc.perform(put("/users/{username}/status", "ghost")
                        .with(authUser("ghost.ghost", List.of("ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).changeStatus(eq("ghost"), any(ProfileStatusChangeDto.class));
        verifyNoMoreInteractions(userService);
    }
}
