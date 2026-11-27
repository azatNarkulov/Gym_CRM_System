package com.epam.workload.controller;

import com.epam.workload.dto.request.WorkloadUpdateRequest;
import com.epam.workload.dto.response.WorkloadSummaryResponse;
import com.epam.workload.domain.MonthlySummary;
import com.epam.workload.domain.WorkloadChange;
import com.epam.workload.service.TrainerWorkloadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import com.epam.workload.exception.WorkloadUsernameMismatchException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api")
@Tag(name = "Trainer Workload", description = "Trainer monthly training workload")
public class TrainerWorkloadController {

    private final TrainerWorkloadService service;

    public TrainerWorkloadController(TrainerWorkloadService service) {
        this.service = service;
    }

    @Operation(summary = "Add or remove a training from trainer workload")
    @PostMapping("/trainers/{username}/workload")
    public ResponseEntity<Void> updateWorkload(
            @PathVariable String username, @Valid @RequestBody WorkloadUpdateRequest request) {
        if (!username.equals(request.trainerUsername())) {
            throw new WorkloadUsernameMismatchException();
        }
        service.updateWorkload(new WorkloadChange(username, request.trainerFirstName(),
                request.trainerLastName(), request.isActive(), request.trainingDate(), request.trainingDuration(),
                WorkloadChange.Action.valueOf(request.actionType().name())));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get a trainer's workload for a month")
    @GetMapping("/trainers/{username}/workload")
    public ResponseEntity<WorkloadSummaryResponse> getMonthlyWorkload(
            @PathVariable String username,
            @RequestParam @Min(1) @Max(9999) int year,
            @RequestParam @Min(1) @Max(12) int month) {
        MonthlySummary summary = service.getMonthlyWorkload(username, year, month);
        BigDecimal hours = BigDecimal.valueOf(summary.duration().getSeconds())
                .add(BigDecimal.valueOf(summary.duration().getNano(), 9))
                .divide(BigDecimal.valueOf(3600), 12, RoundingMode.HALF_UP).stripTrailingZeros();
        return ResponseEntity.ok(new WorkloadSummaryResponse(summary.username(), summary.firstName(),
                summary.lastName(), summary.active(), List.of(new WorkloadSummaryResponse.YearSummary(year,
                List.of(new WorkloadSummaryResponse.MonthSummary(month, hours))))));
    }
}
