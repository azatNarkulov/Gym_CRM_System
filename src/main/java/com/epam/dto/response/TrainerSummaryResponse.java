package com.epam.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TrainerSummaryResponse {
    private String username;
    private String firstName;
    private String lastName;
    private TrainingTypeResponse specialization;
}
