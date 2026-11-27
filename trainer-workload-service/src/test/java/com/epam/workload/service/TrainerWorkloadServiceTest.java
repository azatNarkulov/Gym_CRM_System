package com.epam.workload.service;

import com.epam.workload.domain.WorkloadChange;
import com.epam.workload.exception.InvalidWorkloadChangeException;
import com.epam.workload.exception.TrainerWorkloadNotFoundException;
import com.epam.workload.service.impl.TrainerWorkloadServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class TrainerWorkloadServiceTest {
    private TrainerWorkloadService service;

    @BeforeEach
    void setUp() {
        service = new TrainerWorkloadServiceImpl();
    }

    @Test
    void addsAndSubtractsMonthlyDurations() {
        service.updateWorkload(change(WorkloadChange.Action.ADD, Duration.ofMinutes(90)));
        service.updateWorkload(change(WorkloadChange.Action.ADD, Duration.ofHours(1)));
        service.updateWorkload(change(WorkloadChange.Action.DELETE, Duration.ofHours(1)));
        assertEquals(Duration.ofMinutes(90), service.getMonthlyWorkload("trainer.username", 2026, 11).duration());
        assertEquals(Duration.ZERO, service.getMonthlyWorkload("trainer.username", 2026, 12).duration());
    }

    @Test
    void rejectsNegativeWorkloadWithoutChangingExistingData() {
        service.updateWorkload(change(WorkloadChange.Action.ADD, Duration.ofHours(1)));
        assertThrows(InvalidWorkloadChangeException.class,
                () -> service.updateWorkload(change(WorkloadChange.Action.DELETE, Duration.ofHours(2))));
        assertEquals(Duration.ofHours(1), service.getMonthlyWorkload("trainer.username", 2026, 11).duration());
    }

    @Test
    void rejectsDeleteAndGetForUnknownTrainer() {
        assertThrows(InvalidWorkloadChangeException.class,
                () -> service.updateWorkload(change(WorkloadChange.Action.DELETE, Duration.ofHours(1))));
        assertThrows(TrainerWorkloadNotFoundException.class,
                () -> service.getMonthlyWorkload("trainer.username", 2026, 11));
    }

    @Test
    void concurrentUpdatesDoNotLoseDurations() throws Exception {
        try (ExecutorService executor = Executors.newFixedThreadPool(4)) {
            for (int i = 0; i < 100; i++) {
                executor.submit(() -> service.updateWorkload(change(WorkloadChange.Action.ADD, Duration.ofMinutes(1))));
            }
            executor.shutdown();
            assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
        }
        assertEquals(Duration.ofMinutes(100), service.getMonthlyWorkload("trainer.username", 2026, 11).duration());
    }

    private WorkloadChange change(WorkloadChange.Action action, Duration duration) {
        return new WorkloadChange("trainer.username", "First", "Last", true,
                LocalDate.of(2026, 11, 20), duration, action);
    }
}
