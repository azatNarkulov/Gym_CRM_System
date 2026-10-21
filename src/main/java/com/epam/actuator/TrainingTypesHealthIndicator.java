package com.epam.actuator;

import com.epam.repository.TrainingTypeDao;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class TrainingTypesHealthIndicator implements HealthIndicator {

    private final TrainingTypeDao trainingTypeDao;

    public TrainingTypesHealthIndicator(TrainingTypeDao trainingTypeDao) {
        this.trainingTypeDao = trainingTypeDao;
    }

    @Override
    public Health health() {
        try {
            int count = trainingTypeDao.findAll().size();
            if (count >= 5) {
                return Health.up()
                        .withDetail("trainingTypesCount", count)
                        .withDetail("message", "Training types dictionary is fully loaded")
                        .build();
            } else {
                return Health.down()
                        .withDetail("trainingTypesCount", count)
                        .withDetail("message", "Training types dictionary is incomplete")
                        .build();
            }
        } catch (Exception e) {
            return Health.down(e).build();
        }
    }
}
