package com.danya.user;

import com.danya.exception.EntityNotFoundException;
import com.danya.exception.InvalidCurrentPasswordException;
import com.danya.user.dto.PasswordChangeDto;
import com.danya.user.dto.ProfileStatusChangeDto;
import com.danya.user.role.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void generateUsernameReturnsBaseUsernameWhenNotTaken() {
        String firstName = "Alice";
        String lastName = "Jones";
        String base = firstName + "." + lastName;
        when(userRepository.existsByUsername(base)).thenReturn(false);

        String username = userService.generateUsername(firstName, lastName);

        assertEquals(base, username);
        verify(userRepository).existsByUsername(base);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void generateUsernameAppendsSuffixWhenBaseAlreadyExists() {
        String firstName = "Alice";
        String lastName = "Jones";
        String base = firstName + "." + lastName;
        String candidate1 = base + "1";

        when(userRepository.existsByUsername(base)).thenReturn(true);
        when(userRepository.existsByUsername(candidate1)).thenReturn(false);

        String username = userService.generateUsername(firstName, lastName);

        assertEquals(candidate1, username);
        verify(userRepository).existsByUsername(base);
        verify(userRepository).existsByUsername(candidate1);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void generateUsernameIncrementsSuffixUntilUnique() {
        String firstName = "Bob";
        String lastName = "Brown";
        String base = firstName + "." + lastName;
        String candidate1 = base + "1";
        String candidate2 = base + "2";

        when(userRepository.existsByUsername(base)).thenReturn(true);
        when(userRepository.existsByUsername(candidate1)).thenReturn(true);
        when(userRepository.existsByUsername(candidate2)).thenReturn(false);

        String username = userService.generateUsername(firstName, lastName);

        assertEquals(candidate2, username);
        verify(userRepository).existsByUsername(base);
        verify(userRepository).existsByUsername(candidate1);
        verify(userRepository).existsByUsername(candidate2);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void changePasswordThrowsEntityNotFoundWhenUserMissing() {
        String username = "username";
        PasswordChangeDto payload =
                new PasswordChangeDto("oldPwd", "newPwd");

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> userService.changePassword(username, payload)
        );
        assertEquals("User profile not found", ex.getMessage());

        verify(userRepository, times(1)).findByUsername(username);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void changePasswordThrowsInvalidCurrentPasswordWhenMismatch() {
        User user = new User("First", "Last", "bob", "correctPwd");
        PasswordChangeDto payload =
                new PasswordChangeDto("wrongPwd", "newPwd");

        when(userRepository.findByUsername(user.getUsername()))
                .thenReturn(Optional.of(user));

        assertThrows(
                InvalidCurrentPasswordException.class,
                () -> userService.changePassword(user.getUsername(), payload)
        );

        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void changePasswordUpdatesPasswordAndSavesWhenOldMatches() {
        User user = new User("Alice", "Smith", "alice", "oldPwd");
        PasswordChangeDto payload =
                new PasswordChangeDto("oldPwd", "newPwd");

        when(userRepository.findByUsername(user.getUsername()))
                .thenReturn(Optional.of(user));
        when(userRepository.save(user))
                .thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() -> userService.changePassword(user.getUsername(), payload));

        assertEquals("newPwd", user.getPassword());
        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(userRepository, times(1)).save(user);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void changeStatusThrowsEntityNotFoundWhenUserMissing() {
        String username = "username";
        ProfileStatusChangeDto payload = new ProfileStatusChangeDto(ActivationStatus.ACTIVE);

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> userService.changeStatus(username, payload)
        );
        assertEquals("User profile not found", ex.getMessage());

        verify(userRepository, times(1)).findByUsername(username);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void changeStatusUpdatesActiveFlagAndSavesWhenUserExists() {
        User user = new User("First", "Last", "alice", "pwd");
        user.setActive(false);
        ProfileStatusChangeDto payload = new ProfileStatusChangeDto(ActivationStatus.ACTIVE);

        when(userRepository.findByUsername("alice"))
                .thenReturn(Optional.of(user));
        when(userRepository.save(user))
                .thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() -> userService.changeStatus(user.getUsername(), payload));

        assertTrue(user.isActive());
        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(userRepository, times(1)).save(user);
        verifyNoMoreInteractions(userRepository);
    }
}
