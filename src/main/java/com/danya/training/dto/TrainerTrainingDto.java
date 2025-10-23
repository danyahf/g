package com.danya.training.dto;

import com.danya.trainingType.TrainingType;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record TrainerTrainingDto(
        String trainingName,
        LocalDate date,
        TrainingType trainingType,
        Integer duration,
        Long traineeId
) {
}
