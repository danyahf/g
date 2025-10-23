package com.danya.auth;

import com.danya.exception.BadCredentialsException;
import com.danya.user.User;
import com.danya.user.UserRepository;
import com.danya.user.dto.CredentialsDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void authenticateThrowsBadCredentialsWhenUserMissing() {
        CredentialsDto payload = new CredentialsDto("noone.nobody", "secret");
        when(userRepository.findByUsername("noone.nobody")).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class,
                () -> authService.authenticate(payload));

        verify(userRepository, times(1)).findByUsername("noone.nobody");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void authenticateThrowsBadCredentialsWhenPasswordMismatch() {
        User user = new User();
        user.setUsername("alice.john");
        user.setPassword("correct");
        CredentialsDto payload = new CredentialsDto("alice.john", "wrong");

        when(userRepository.findByUsername("alice.john")).thenReturn(Optional.of(user));

        assertThrows(BadCredentialsException.class,
                () -> authService.authenticate(payload));

        verify(userRepository, times(1)).findByUsername("alice.john");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void authenticateSucceedsReturnsAccessTokenWhenPasswordMatches() {
        User user = new User();
        user.setUsername("alice.john");
        user.setPassword("pw123");
        CredentialsDto payload = new CredentialsDto("alice.john", "pw123");

        when(userRepository.findByUsername("alice.john")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwt-token-123");

        TokenDto tokenDto = authService.authenticate(payload);

        assertNotNull(tokenDto);
        assertEquals("jwt-token-123", tokenDto.getAccessToken());
        verify(userRepository, times(1)).findByUsername("alice.john");
        verify(jwtService, times(1)).generateToken(user);
        verifyNoMoreInteractions(userRepository, jwtService);
    }
}
