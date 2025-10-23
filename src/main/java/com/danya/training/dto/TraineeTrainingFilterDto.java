package com.danya.training.dto;


import lombok.Builder;

import java.time.LocalDate;

@Builder
public record TraineeTrainingFilterDto(
        String traineeUsername,
        LocalDate fromDate,
        LocalDate toDate,
        String trainerUsername,
        Integer trainingTypeId
) {
}
