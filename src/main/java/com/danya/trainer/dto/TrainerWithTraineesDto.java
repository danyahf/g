package com.danya.trainer.dto;

import com.danya.trainingType.TrainingType;
import lombok.Builder;

import java.util.List;

@Builder
public record TrainerWithTraineesDto(
        String firstName,
        String lastName,
        TrainingType specialization,
        boolean isActive,
        List<TrainerProfileTraineeDto> trainees
) {
}
