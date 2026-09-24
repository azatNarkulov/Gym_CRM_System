package com.epam.domain;

import lombok.Data;

@Data
public class Trainer extends User {
    private TrainingType specialization;
}
