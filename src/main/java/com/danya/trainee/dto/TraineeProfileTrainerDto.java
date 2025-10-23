package com.danya.trainee.dto;

import com.danya.trainingType.TrainingType;
import lombok.Builder;

@Builder
public record TraineeProfileTrainerDto(
        String username,
        String firstName,
        String lastName,
        TrainingType specialization
) {
}
