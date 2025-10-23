package com.danya.user.api;

import com.danya.security.authentication.AuthUser;
import com.danya.security.authentication.AuthenticatedUser;
import com.danya.security.authorization.RequiredRoles;
import com.danya.user.UserService;
import com.danya.user.dto.PasswordChangeDto;
import com.danya.user.dto.ProfileStatusChangeDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController implements UserApi {
    private final UserService userService;

    @PutMapping("/{username}/password")
    @RequiredRoles({"ADMIN"})
    public ResponseEntity<Void> changePassword(
            @PathVariable String username,
            @RequestBody @Valid PasswordChangeDto payload
    ) {
        userService.changePassword(username, payload);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PutMapping("/me/password")
    @RequiredRoles({"TRAINER", "TRAINEE"})
    public ResponseEntity<Void> changePassword(
            @AuthenticatedUser AuthUser authUser,
            @RequestBody @Valid PasswordChangeDto payload
    ) {
        userService.changePassword(authUser.getUsername(), payload);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PutMapping("/{username}/status")
    @RequiredRoles({"ADMIN"})
    public ResponseEntity<Void> changeStatus(
            @PathVariable String username,
            @RequestBody @Valid ProfileStatusChangeDto payload
    ) {
        userService.changeStatus(username, payload);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PutMapping("/me/status")
    @RequiredRoles({"TRAINER", "TRAINEE"})
    public ResponseEntity<Void> changeStatus(
            @AuthenticatedUser AuthUser authUser,
            @RequestBody @Valid ProfileStatusChangeDto payload
    ) {
        userService.changeStatus(authUser.getUsername(), payload);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }
}
