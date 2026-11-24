package com.epam.workload.controller;

import com.epam.workload.dto.request.WorkloadUpdateRequest;
import com.epam.workload.dto.response.WorkloadSummaryResponse;
import com.epam.workload.service.TrainerWorkloadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    @PostMapping("/workload")
    public ResponseEntity<Void> updateWorkload(@Valid @RequestBody WorkloadUpdateRequest request) {
        service.updateWorkload(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get a trainer's workload for a month")
    @GetMapping("/trainers/{username}/workload")
    public ResponseEntity<WorkloadSummaryResponse> getMonthlyWorkload(
            @PathVariable String username,
            @RequestParam @Min(1) @Max(9999) int year,
            @RequestParam @Min(1) @Max(12) int month) {
        return ResponseEntity.ok(service.getMonthlyWorkload(username, year, month));
    }
}
