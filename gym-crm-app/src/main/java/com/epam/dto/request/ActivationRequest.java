package com.epam.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivationRequest {

    @NotNull(message = "Is active is required")
    private Boolean isActive;
}
