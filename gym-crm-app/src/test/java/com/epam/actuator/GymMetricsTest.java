package com.epam.actuator;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GymMetricsTest {

    private static final String REGISTRATIONS_METRIC_NAME = "gym_registrations_total";
    private static final String ROLE_TAG = "role";

    private MeterRegistry registry;
    private GymMetrics gymMetrics;

    @BeforeEach
    public void setUp() {
        registry = new SimpleMeterRegistry();
        gymMetrics = new GymMetrics(registry);
    }

    @Test
    public void shouldIncrementTraineeRegistrations() {
        gymMetrics.incrementTraineeRegistrations();

        Counter counter = registry.find(REGISTRATIONS_METRIC_NAME).tag(ROLE_TAG, "trainee").counter();
        assertNotNull(counter);
        assertEquals(1.0, counter.count());
    }

    @Test
    public void shouldIncrementTrainerRegistrations() {
        gymMetrics.incrementTrainerRegistrations();
        gymMetrics.incrementTrainerRegistrations();

        Counter counter = registry.find(REGISTRATIONS_METRIC_NAME).tag(ROLE_TAG, "trainer").counter();
        assertNotNull(counter);
        assertEquals(2.0, counter.count());
    }

    @Test
    public void shouldIncrementTrainingRegistrations() {
        gymMetrics.incrementTrainingRegistrations();

        Counter counter = registry.find("gym_training_created_total").counter();
        assertNotNull(counter);
        assertEquals(1.0, counter.count());
    }
}
