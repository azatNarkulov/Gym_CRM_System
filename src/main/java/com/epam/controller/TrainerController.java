package com.epam.controller;

import com.epam.domain.Trainer;
import com.epam.domain.Training;
import com.epam.domain.TrainingType;
import com.epam.domain.TrainingTypeName;
import com.epam.dto.TrainingFilterDTO;
import com.epam.dto.request.ActivationRequest;
import com.epam.dto.request.TrainerRegistrationRequest;
import com.epam.dto.request.UpdateTrainerProfileRequest;
import com.epam.dto.response.RegistrationResponse;
import com.epam.dto.response.TrainerProfileResponse;
import com.epam.dto.response.TrainerTrainingResponse;
import com.epam.exception.AccessDeniedException;
import com.epam.filter.AuthenticationFilter;
import com.epam.mapper.TrainerMapper;
import com.epam.mapper.TrainingMapper;
import com.epam.service.TrainerService;
import com.epam.service.TrainingService;
import com.epam.service.TrainingTypeService;
import com.epam.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Trainer", description = "Trainer operations")
@RestController
@RequestMapping("/api/trainers")
public class TrainerController {

    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private final TrainingTypeService trainingTypeService;
    private final UserService userService;

    public TrainerController(TrainerService trainerService,
                             TrainingService trainingService,
                             TrainingTypeService trainingTypeService,
                             UserService userService) {
        this.trainerService = trainerService;
        this.trainingService = trainingService;
        this.trainingTypeService = trainingTypeService;
        this.userService = userService;
    }

    @Operation(summary = "Register new trainer profile")
    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> register(
            @Valid @RequestBody TrainerRegistrationRequest request) {
        TrainingType specialization = trainingTypeService.findByName(TrainingTypeName.valueOf(request.getSpecialization()));

        Trainer trainer = new Trainer();
        trainer.setFirstName(request.getFirstName());
        trainer.setLastName(request.getLastName());
        trainer.setSpecialization(specialization);

        Trainer created = trainerService.add(trainer);
        return ResponseEntity.ok(new RegistrationResponse(created.getUsername(), created.getPassword()));
    }

    @Operation(summary = "Get trainer profile")
    @GetMapping("/{username}")
    public ResponseEntity<TrainerProfileResponse> getProfile(
            @PathVariable String username,
            @RequestAttribute(AuthenticationFilter.AUTHENTICATED_USERNAME_ATTR) String authenticated) {
        requireSelf(username, authenticated);
        Trainer trainer = trainerService.findByUsername(username);
        return ResponseEntity.ok(TrainerMapper.toProfile(trainer));
    }

    @Operation(summary = "Update trainer profile")
    @PutMapping("/{username}")
    public ResponseEntity<TrainerProfileResponse> updateProfile(
            @PathVariable String username,
            @RequestAttribute(AuthenticationFilter.AUTHENTICATED_USERNAME_ATTR) String authenticated,
            @Valid @RequestBody UpdateTrainerProfileRequest request) {
        requireSelf(username, authenticated);

        Trainer trainer = trainerService.findByUsername(username);
        trainer.setFirstName(request.getFirstName());
        trainer.setLastName(request.getLastName());
        trainer.setActive(request.getIsActive());

        Trainer updated = trainerService.update(trainer);
        return ResponseEntity.ok(TrainerMapper.toProfile(updated));
    }

    @Operation(summary = "Get trainer trainings with optional filters")
    @GetMapping("/{username}/trainings")
    public ResponseEntity<List<TrainerTrainingResponse>> getTrainings(
            @PathVariable String username,
            @RequestAttribute(AuthenticationFilter.AUTHENTICATED_USERNAME_ATTR) String authenticated,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) String traineeName) {
        requireSelf(username, authenticated);

        TrainingFilterDTO filter = new TrainingFilterDTO(username, fromDate, toDate, traineeName, null);
        List<Training> trainings = trainingService.getTrainerTrainings(username, filter);

        return ResponseEntity.ok(trainings.stream().map(TrainingMapper::toTrainerTraining).toList());
    }

    @Operation(summary = "Activate or deactivate trainer")
    @PatchMapping("/{username}/activate")
    public ResponseEntity<Void> setActive(
            @PathVariable String username,
            @RequestAttribute(AuthenticationFilter.AUTHENTICATED_USERNAME_ATTR) String authenticated,
            @Valid @RequestBody ActivationRequest request) {
        requireSelf(username, authenticated);
        userService.setActive(username, request.getIsActive());
        return ResponseEntity.ok().build();
    }

    private void requireSelf(String username, String authenticated) {
        if (!username.equals(authenticated)) {
            throw new AccessDeniedException("Access denied to another user's data");
        }
    }
}
