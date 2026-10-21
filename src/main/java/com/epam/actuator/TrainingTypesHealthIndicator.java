package com.epam.actuator;

import com.epam.domain.TrainingType;
import com.epam.domain.TrainingTypeName;
import com.epam.repository.TrainingTypeDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TrainingTypesHealthIndicator implements HealthIndicator {

    private static final Logger LOG = LoggerFactory.getLogger(TrainingTypesHealthIndicator.class);

    private final TrainingTypeDao trainingTypeDao;

    public TrainingTypesHealthIndicator(TrainingTypeDao trainingTypeDao) {
        this.trainingTypeDao = trainingTypeDao;
    }

    @Override
    public Health health() {
        try {
            Set<TrainingTypeName> actualTypes = trainingTypeDao.findAll().stream()
                    .map(TrainingType::getName)
                    .collect(Collectors.toSet());

            Set<TrainingTypeName> expectedTypes = EnumSet.allOf(TrainingTypeName.class);

            int count = actualTypes.size();

            if (actualTypes.equals(expectedTypes)) {
                return Health.up()
                        .withDetail("trainingTypesCount", count)
                        .withDetail("message", "Training types dictionary is fully loaded")
                        .build();
            } else {
                LOG.warn("Training types is incomplete or unexpected: found={}, expected={}", actualTypes, expectedTypes);
                return Health.down()
                        .withDetail("trainingTypesCount", count)
                        .withDetail("message", "Training types dictionary is incomplete")
                        .build();
            }
        } catch (Exception e) {
            LOG.error("Training types health check failed", e);
            return Health.down(e).build();
        }
    }
}
