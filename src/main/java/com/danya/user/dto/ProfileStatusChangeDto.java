package com.danya.user.dto;

import com.danya.user.ActivationStatus;

public record ProfileStatusChangeDto(
        ActivationStatus status
) {
}
