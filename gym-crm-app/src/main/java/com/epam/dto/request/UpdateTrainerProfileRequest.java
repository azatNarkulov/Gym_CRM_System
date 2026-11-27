package com.epam.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTrainerProfileRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String specialization;

    @NotNull(message = "Is active is required")
    private Boolean isActive;
}
