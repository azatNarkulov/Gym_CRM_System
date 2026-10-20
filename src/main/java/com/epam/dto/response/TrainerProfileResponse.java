package com.epam.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class TrainerProfileResponse {
    private String username;
    private String firstName;
    private String lastName;
    private TrainingTypeResponse specialization;
    private Boolean isActive;
    private List<TraineeSummaryResponse> trainees;
}
