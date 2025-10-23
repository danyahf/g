package com.danya.training.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record TrainerTrainingFilterDto(
        String trainerUsername,
        LocalDate fromDate,
        LocalDate toDate,
        String traineeUsername
) {
}
