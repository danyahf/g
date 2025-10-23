package com.danya.trainee.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Date;

public record UpdateTraineeDto(
        @NotNull(message = "Firstname cannot be null")
        @NotBlank(message = "Firstname cannot be blank")
        @Size(min = 2, max = 55, message = "Firstname must be between 2 and 55 characters")
        String firstName,

        @NotNull(message = "Lastname cannot be null")
        @NotBlank(message = "Lastname cannot be blank")
        @Size(min = 2, max = 55, message = "Lastname must be between 2 and 55 characters")
        String lastName,

        Date dateOfBirth,
        String address,

        @NotNull(message = "Status cannot be null")
        Boolean isActive
) {
}
