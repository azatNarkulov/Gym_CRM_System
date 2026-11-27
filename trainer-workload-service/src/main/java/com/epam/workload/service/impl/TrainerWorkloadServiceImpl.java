package com.epam.workload.service.impl;

import com.epam.workload.domain.MonthlySummary;
import com.epam.workload.domain.TrainerWorkload;
import com.epam.workload.domain.WorkloadChange;
import com.epam.workload.exception.InvalidWorkloadChangeException;
import com.epam.workload.exception.TrainerWorkloadNotFoundException;
import com.epam.workload.service.TrainerWorkloadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

@Service
public class TrainerWorkloadServiceImpl implements TrainerWorkloadService {
    private static final Logger LOG = LoggerFactory.getLogger(TrainerWorkloadServiceImpl.class);
    private final Map<String, TrainerWorkload> trainers = new HashMap<>();

    @Override
    public synchronized void updateWorkload(WorkloadChange change) {
        YearMonth month = YearMonth.from(change.trainingDate());
        TrainerWorkload trainer = trainers.get(change.trainerUsername());
        Duration previous = trainer == null ? Duration.ZERO : trainer.duration(month);
        if (change.action() == WorkloadChange.Action.DELETE
                && (trainer == null || previous.compareTo(change.trainingDuration()) < 0)) {
            throw new InvalidWorkloadChangeException();
        }
        Duration updated = change.action() == WorkloadChange.Action.ADD
                ? previous.plus(change.trainingDuration()) : previous.minus(change.trainingDuration());
        if (trainer == null) {
            trainer = new TrainerWorkload(change.trainerUsername(), change.trainerFirstName(),
                    change.trainerLastName(), change.active());
            trainers.put(change.trainerUsername(), trainer);
        }
        trainer.updateProfile(change.trainerFirstName(), change.trainerLastName(), change.active());
        trainer.setDuration(month, updated);
        LOG.info("Applied workload action={} period={} duration={}", change.action(), month, change.trainingDuration());
    }

    @Override
    public synchronized MonthlySummary getMonthlyWorkload(String username, int year, int month) {
        TrainerWorkload trainer = trainers.get(username);
        if (trainer == null) {
            throw new TrainerWorkloadNotFoundException(username);
        }
        YearMonth period = YearMonth.of(year, month);
        LOG.info("Returning monthly workload period={}", period);
        return new MonthlySummary(trainer.username(), trainer.firstName(), trainer.lastName(),
                trainer.active(), period, trainer.duration(period));
    }
}
