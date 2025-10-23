package com.danya.trainee.dto;

import lombok.Builder;

import java.util.Date;
import java.util.List;

@Builder
public record TraineeDto(
        String firstName,
        String lastName,
        Date dateOfBirth,
        String address,
        boolean isActive,
        List<TraineeProfileTrainerDto> trainers
) {
}
