package com.epam.workload.service;

import com.epam.workload.domain.MonthlyWorkload;
import com.epam.workload.domain.TrainerWorkload;
import com.epam.workload.dto.request.WorkloadAction;
import com.epam.workload.dto.request.WorkloadUpdateRequest;
import com.epam.workload.exception.InvalidWorkloadChangeException;
import com.epam.workload.exception.TrainerWorkloadNotFoundException;
import com.epam.workload.repository.TrainerWorkloadRepository;
import com.epam.workload.service.impl.TrainerWorkloadServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadServiceTest {

    private static final String USERNAME = "trainer.username";

    @Mock
    private TrainerWorkloadRepository repository;

    private TrainerWorkloadService service;

    @BeforeEach
    public void setUp() {
        service = new TrainerWorkloadServiceImpl(repository);
    }

    @Test
    public void shouldAddTrainingDurationForTrainerAndMonth() {
        when(repository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        service.updateWorkload(request(WorkloadAction.ADD, Duration.ofMinutes(90)));

        verify(repository).save(org.mockito.ArgumentMatchers.argThat(trainer ->
                trainer.getUsername().equals(USERNAME)
                        && trainer.getMonthlyWorkloads().size() == 1
                        && trainer.getMonthlyWorkloads().getFirst().getTrainingSummaryDuration()
                        .equals(Duration.ofMinutes(90))));
    }

    @Test
    public void shouldSubtractCancelledTrainingDuration() {
        TrainerWorkload trainer = new TrainerWorkload(USERNAME, "First", "Last", true);
        MonthlyWorkload workload = new MonthlyWorkload(2026, 11);
        workload.addDuration(Duration.ofHours(3));
        trainer.addMonthlyWorkload(workload);
        when(repository.findByUsername(USERNAME)).thenReturn(Optional.of(trainer));

        service.updateWorkload(request(WorkloadAction.DELETE, Duration.ofHours(1)));

        assertEquals(Duration.ofHours(2), workload.getTrainingSummaryDuration());
        verify(repository).save(trainer);
    }

    @Test
    public void shouldRejectDeletionThatWouldMakeMonthlyDurationNegative() {
        TrainerWorkload trainer = new TrainerWorkload(USERNAME, "First", "Last", true);
        MonthlyWorkload workload = new MonthlyWorkload(2026, 11);
        workload.addDuration(Duration.ofHours(1));
        trainer.addMonthlyWorkload(workload);
        when(repository.findByUsername(USERNAME)).thenReturn(Optional.of(trainer));

        assertThrows(InvalidWorkloadChangeException.class,
                () -> service.updateWorkload(request(WorkloadAction.DELETE, Duration.ofHours(2))));
    }

    @Test
    public void shouldReturnMonthlyDurationInHoursUsingYearMonthHierarchy() {
        TrainerWorkload trainer = new TrainerWorkload(USERNAME, "First", "Last", true);
        MonthlyWorkload workload = new MonthlyWorkload(2026, 11);
        workload.addDuration(Duration.ofMinutes(90));
        trainer.addMonthlyWorkload(workload);
        when(repository.findByUsername(USERNAME)).thenReturn(Optional.of(trainer));

        var summary = service.getMonthlyWorkload(USERNAME, 2026, 11);

        assertEquals("1.5", summary.years().getFirst().months().getFirst()
                .trainingSummaryDurationHours().toPlainString());
    }

    @Test
    public void shouldReturnZeroForMonthWithoutTrainings() {
        when(repository.findByUsername(USERNAME))
                .thenReturn(Optional.of(new TrainerWorkload(USERNAME, "First", "Last", true)));

        var summary = service.getMonthlyWorkload(USERNAME, 2026, 11);

        assertEquals(0, summary.years().getFirst().months().getFirst()
                .trainingSummaryDurationHours().compareTo(java.math.BigDecimal.ZERO));
    }

    @Test
    public void shouldFailWhenTrainerDoesNotExist() {
        when(repository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        assertThrows(TrainerWorkloadNotFoundException.class,
                () -> service.getMonthlyWorkload(USERNAME, 2026, 11));
    }

    private WorkloadUpdateRequest request(WorkloadAction action, Duration duration) {
        return new WorkloadUpdateRequest(
                USERNAME, "First", "Last", true, LocalDate.of(2026, 11, 20), duration, action);
    }
}
