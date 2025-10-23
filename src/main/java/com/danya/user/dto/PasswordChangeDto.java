package com.danya.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PasswordChangeDto(
        @NotNull(message = "Old password cannot be null")
        @NotBlank(message = "Old password cannot be blank")
        String oldPassword,

        @NotNull(message = "New password cannot be null")
        @NotBlank(message = "New password cannot be blank")
        String newPassword
) {
}
