package com.epam.object;

import com.epam.util.TrainingType;
import lombok.Data;

@Data
public class Trainer extends User {
    private TrainingType specialization;
    private Long userId;
}
