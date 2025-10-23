package com.danya.trainer.dto;

import lombok.Builder;

@Builder
public record TrainerProfileTraineeDto(
        String username,
        String firstName,
        String lastName
) {
}
