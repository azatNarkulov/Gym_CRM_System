package com.epam.workload.service;

import com.epam.workload.dto.request.WorkloadAction;
import com.epam.workload.dto.request.WorkloadUpdateRequest;
import com.epam.workload.repository.TrainerWorkloadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = "JWT_SECRET=MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=")
public class TrainerWorkloadPersistenceTest {

    @Autowired
    private TrainerWorkloadService service;

    @Autowired
    private TrainerWorkloadRepository repository;

    @BeforeEach
    public void clearDatabase() {
        repository.deleteAll();
    }

    @Test
    public void shouldPersistDurationAndReturnMonthlySummaryFromH2() {
        service.updateWorkload(request(WorkloadAction.ADD, Duration.ofMinutes(90)));

        var summary = service.getMonthlyWorkload("trainer.username", 2026, 11);

        assertEquals("1.5", summary.years().getFirst().months().getFirst()
                .trainingSummaryDurationHours().toPlainString());
    }

    @Test
    public void shouldPersistWorkloadDeletion() {
        service.updateWorkload(request(WorkloadAction.ADD, Duration.ofHours(2)));
        service.updateWorkload(request(WorkloadAction.DELETE, Duration.ofHours(1)));

        var summary = service.getMonthlyWorkload("trainer.username", 2026, 11);

        assertEquals("1", summary.years().getFirst().months().getFirst()
                .trainingSummaryDurationHours().toPlainString());
    }

    private WorkloadUpdateRequest request(WorkloadAction action, Duration duration) {
        return new WorkloadUpdateRequest(
                "trainer.username",
                "First",
                "Last",
                true,
                LocalDate.of(2026, 11, 20),
                duration,
                action);
    }
}
