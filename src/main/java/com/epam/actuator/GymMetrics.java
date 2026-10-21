package com.epam.actuator;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class GymMetrics {

    private final Counter traineeRegistrationCounter;
    private final Counter trainerRegistrationCounter;
    private final Counter trainingRegistrationCounter;

    public GymMetrics(MeterRegistry registry) {
        this.traineeRegistrationCounter = Counter.builder("gym_registrations_total")
                .description("Total number of registered trainees")
                .tag("role", "trainee")
                .register(registry);

        this.trainerRegistrationCounter = Counter.builder("gym_registrations_total")
                .description("Total number of registered trainers")
                .tag("role", "trainer")
                .register(registry);

        this.trainingRegistrationCounter = Counter.builder("gym_training_created_total")
                .description("Total number of created trainings")
                .register(registry);
    }

    public void incrementTraineeRegistrations() {
        traineeRegistrationCounter.increment();
    }

    public void incrementTrainerRegistrations() {
        trainerRegistrationCounter.increment();
    }

    public void incrementTrainingRegistrations() {
        trainingRegistrationCounter.increment();
    }
}
