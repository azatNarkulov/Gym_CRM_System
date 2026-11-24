package com.epam.workload.service.impl;

import com.epam.workload.domain.MonthlyWorkload;
import com.epam.workload.domain.TrainerWorkload;
import com.epam.workload.dto.request.WorkloadAction;
import com.epam.workload.dto.request.WorkloadUpdateRequest;
import com.epam.workload.dto.response.WorkloadSummaryResponse;
import com.epam.workload.exception.InvalidWorkloadChangeException;
import com.epam.workload.exception.TrainerWorkloadNotFoundException;
import com.epam.workload.repository.TrainerWorkloadRepository;
import com.epam.workload.service.TrainerWorkloadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.YearMonth;
import java.util.List;

@Service
@Transactional
public class TrainerWorkloadServiceImpl implements TrainerWorkloadService {

    private static final Logger LOG = LoggerFactory.getLogger(TrainerWorkloadServiceImpl.class);
    private static final BigDecimal SECONDS_PER_HOUR = BigDecimal.valueOf(3_600);
    private static final int HOURS_SCALE = 12;

    private final TrainerWorkloadRepository repository;

    public TrainerWorkloadServiceImpl(TrainerWorkloadRepository repository) {
        this.repository = repository;
    }

    @Override
    public void updateWorkload(WorkloadUpdateRequest request) {
        YearMonth yearMonth = YearMonth.from(request.trainingDate());
        TrainerWorkload trainer = repository.findByUsername(request.trainerUsername())
                .orElseGet(() -> new TrainerWorkload(
                        request.trainerUsername(),
                        request.trainerFirstName(),
                        request.trainerLastName(),
                        request.isActive()));
        trainer.updateProfile(request.trainerFirstName(), request.trainerLastName(), request.isActive());

        MonthlyWorkload monthlyWorkload = findMonth(trainer, yearMonth);
        if (request.actionType() == WorkloadAction.ADD) {
            if (monthlyWorkload == null) {
                monthlyWorkload = new MonthlyWorkload(yearMonth.getYear(), yearMonth.getMonthValue());
                trainer.addMonthlyWorkload(monthlyWorkload);
            }
            monthlyWorkload.addDuration(request.trainingDuration());
        } else {
            if (monthlyWorkload == null
                    || monthlyWorkload.getTrainingSummaryDuration().compareTo(request.trainingDuration()) < 0) {
                throw new InvalidWorkloadChangeException();
            }
            monthlyWorkload.subtractDuration(request.trainingDuration());
        }

        repository.save(trainer);
        LOG.info("Applied workload action={} period={} duration={}",
                request.actionType(), yearMonth, request.trainingDuration());
    }

    @Override
    @Transactional(readOnly = true)
    public WorkloadSummaryResponse getMonthlyWorkload(String username, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        TrainerWorkload trainer = repository.findByUsername(username)
                .orElseThrow(() -> new TrainerWorkloadNotFoundException(username));
        MonthlyWorkload monthlyWorkload = findMonth(trainer, yearMonth);
        Duration duration = monthlyWorkload == null
                ? Duration.ZERO
                : monthlyWorkload.getTrainingSummaryDuration();
        LOG.info("Returning monthly workload period={}-{}",
                yearMonth.getYear(), yearMonth.getMonthValue());

        WorkloadSummaryResponse.MonthSummary monthSummary =
                new WorkloadSummaryResponse.MonthSummary(month, toHours(duration));
        WorkloadSummaryResponse.YearSummary yearSummary =
                new WorkloadSummaryResponse.YearSummary(year, List.of(monthSummary));

        return new WorkloadSummaryResponse(
                trainer.getUsername(),
                trainer.getFirstName(),
                trainer.getLastName(),
                trainer.isActive(),
                List.of(yearSummary));
    }

    private MonthlyWorkload findMonth(TrainerWorkload trainer, YearMonth yearMonth) {
        return trainer.getMonthlyWorkloads().stream()
                .filter(workload -> workload.getYear() == yearMonth.getYear()
                        && workload.getMonth() == yearMonth.getMonthValue())
                .findFirst()
                .orElse(null);
    }

    private BigDecimal toHours(Duration duration) {
        BigDecimal seconds = BigDecimal.valueOf(duration.getSeconds())
                .add(BigDecimal.valueOf(duration.getNano(), 9));
        return seconds.divide(SECONDS_PER_HOUR, HOURS_SCALE, RoundingMode.HALF_UP)
                .stripTrailingZeros();
    }
}
