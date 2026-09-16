package com.epam.object;

import com.epam.TrainingType;

public class Trainer extends User {
    private TrainingType specialization;
    private Long userId;

    public TrainingType getSpecialization() {
        return specialization;
    }

    public void setSpecialization(TrainingType specialization) {
        this.specialization = specialization;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
