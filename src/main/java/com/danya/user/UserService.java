package com.danya.user;

import com.danya.exception.EntityNotFoundException;
import com.danya.exception.InvalidCurrentPasswordException;
import com.danya.user.dto.PasswordChangeDto;
import com.danya.user.dto.ProfileStatusChangeDto;
import com.danya.user.role.Role;
import com.danya.user.role.RoleName;
import com.danya.user.role.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    //todo: consider a better approach; thread safety
    public String generateUsername(String firstName, String lastName) {
        String username = firstName + "." + lastName;
        String candidate = username;
        int suffix = 1;

        while (userRepository.existsByUsername(candidate)) {
            candidate = username + suffix;
            suffix += 1;
        }

        return candidate;
    }

    public Role findRoleByName(RoleName roleName) {
        return roleRepository.findByRoleName(roleName)
                .orElseThrow();
    }

    @Transactional
    public void changePassword(String username, PasswordChangeDto payload) {
        User user = findByUsername(username);

        boolean matched = user.getPassword().equals(payload.oldPassword());
        if (!matched) {
            log.warn("The provided password does not match  the current one");
            throw new InvalidCurrentPasswordException();
        }

        user.setPassword(payload.newPassword());
        userRepository.save(user);
    }

    @Transactional
    public void changeStatus(String username, ProfileStatusChangeDto payload) {
        User user = findByUsername(username);
        user.setActive(payload.status().isActive());
        userRepository.save(user);
    }

    private User findByUsername(String username) throws EntityNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User with {} username does not exist", username);
                    return new EntityNotFoundException("User profile not found");
                });
    }
}
