package com.epam.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateTraineeTrainersRequest {

    @NotNull(message = "Trainers list is required")
    @Valid
    private List<TrainerUsernameRequest> trainers;
}
