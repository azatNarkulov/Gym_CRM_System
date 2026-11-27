package com.epam.workload.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.time.DurationMin;

import java.time.Duration;
import java.time.LocalDate;

public record WorkloadUpdateRequest(
        @NotBlank String trainerUsername,
        @NotBlank String trainerFirstName,
        @NotBlank String trainerLastName,
        @NotNull Boolean isActive,
        @NotNull LocalDate trainingDate,
        @NotNull @DurationMin(seconds = 1) Duration trainingDuration,
        @NotNull WorkloadAction actionType) {
}
