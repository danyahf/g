package com.danya.training.dto;

import com.danya.trainingType.TrainingTypeName;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record CreateTrainingDto(
        @NotNull(message = "Trainee username cannot be null")
        @NotBlank(message = "Trainee username cannot be blank")
        String traineeUsername,

        @NotNull(message = "Trainer username cannot be null")
        @NotBlank(message = "Trainer username cannot be blank")
        String trainerUsername,

        @NotNull(message = "Training type name cannot be null")
        TrainingTypeName trainingTypeName,

        @NotNull(message = "Training name cannot be null")
        @NotBlank(message = "Training name cannot be blank")
        @Size(min = 2, max = 75, message = "Training name must be between 2 and 75 characters")
        String name,

        @NotNull(message = "Training date cannot be null")
        @FutureOrPresent(message = "Training date cannot be in the past")
        LocalDate trainingDate,

        @NotNull(message = "Training duration cannot be null")
        @Min(value = 15, message = "The training must last at least 15 minutes.")
        @Max(value = 180, message = "The training must not last longer than 180 minutes")
        Integer duration
) {
}
