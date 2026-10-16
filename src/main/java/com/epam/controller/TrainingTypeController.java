package com.epam.controller;

import com.epam.dto.response.TrainingTypeResponse;
import com.epam.mapper.TrainingTypeMapper;
import com.epam.service.TrainingTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Training Type", description = "Training type dictionary")
@RestController
@RequestMapping("/api/training-types")
public class TrainingTypeController {

    private final TrainingTypeService trainingTypeService;

    public TrainingTypeController(TrainingTypeService trainingTypeService) {
        this.trainingTypeService = trainingTypeService;
    }

    @Operation(summary = "Get all training types")
    @GetMapping
    public ResponseEntity<List<TrainingTypeResponse>> getAll() {
        List<TrainingTypeResponse> response = trainingTypeService.getAll().stream()
                .map(TrainingTypeMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }
}
