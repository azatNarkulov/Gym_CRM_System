package com.epam.dto;

import com.epam.domain.TrainingTypeName;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@EqualsAndHashCode
public class TrainingFilterDTO {
    private String username;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String otherUsername;
    private TrainingTypeName trainingType;

    public TrainingFilterDTO(String username, LocalDate fromDate, LocalDate toDate, String otherUsername, TrainingTypeName trainingType) {
        this.username = username;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.otherUsername = otherUsername;
        this.trainingType = trainingType;
    }
}
