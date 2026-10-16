package com.epam.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainerUsernameRequest {

    @NotBlank(message = "Trainer username is required")
    private String trainerUsername;
}
