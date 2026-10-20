package com.epam.controller;

import com.epam.domain.Trainee;
import com.epam.domain.Trainer;
import com.epam.domain.Training;
import com.epam.domain.TrainingTypeName;
import com.epam.dto.TrainingFilterDTO;
import com.epam.dto.request.ActivationRequest;
import com.epam.dto.request.TraineeRegistrationRequest;
import com.epam.dto.request.TrainerUsernameRequest;
import com.epam.dto.request.UpdateTraineeProfileRequest;
import com.epam.dto.request.UpdateTraineeTrainersRequest;
import com.epam.dto.response.NotAssignedTrainerResponse;
import com.epam.dto.response.RegistrationResponse;
import com.epam.dto.response.TraineeProfileResponse;
import com.epam.dto.response.TraineeTrainingResponse;
import com.epam.dto.response.TrainerSummaryResponse;
import com.epam.exception.AccessDeniedException;
import com.epam.filter.AuthenticationFilter;
import com.epam.mapper.TraineeMapper;
import com.epam.mapper.TrainerMapper;
import com.epam.mapper.TrainingMapper;
import com.epam.service.TraineeService;
import com.epam.service.TrainerService;
import com.epam.service.TrainingService;
import com.epam.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Trainee", description = "Trainee operations")
@RestController
@RequestMapping("/api/trainees")
public class TraineeController {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private final UserService userService;

    public TraineeController(TraineeService traineeService,
                             TrainerService trainerService,
                             TrainingService trainingService,
                             UserService userService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
        this.userService = userService;
    }

    @Operation(summary = "Register new trainee profile")
    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> register(
            @Valid @RequestBody TraineeRegistrationRequest request) {
        Trainee trainee = new Trainee();
        trainee.setFirstName(request.getFirstName());
        trainee.setLastName(request.getLastName());
        trainee.setDateOfBirth(request.getDateOfBirth());
        trainee.setAddress(request.getAddress());

        Trainee created = traineeService.add(trainee);
        return ResponseEntity.ok(new RegistrationResponse(created.getUsername(), created.getPassword()));
    }

    @Operation(summary = "Get trainee profile")
    @GetMapping("/{username}")
    public ResponseEntity<TraineeProfileResponse> getProfile(
            @PathVariable String username,
            @RequestAttribute(AuthenticationFilter.AUTHENTICATED_USERNAME_ATTR) String authenticated) {
        requireSelf(username, authenticated);
        Trainee trainee = traineeService.findByUsername(username);
        return ResponseEntity.ok(TraineeMapper.toProfile(trainee));
    }

    @Operation(summary = "Update trainee profile")
    @PutMapping("/{username}")
    public ResponseEntity<TraineeProfileResponse> updateProfile(
            @PathVariable String username,
            @RequestAttribute(AuthenticationFilter.AUTHENTICATED_USERNAME_ATTR) String authenticated,
            @Valid @RequestBody UpdateTraineeProfileRequest request) {
        requireSelf(username, authenticated);

        Trainee trainee = traineeService.findByUsername(username);
        trainee.setFirstName(request.getFirstName());
        trainee.setLastName(request.getLastName());
        trainee.setDateOfBirth(request.getDateOfBirth());
        trainee.setAddress(request.getAddress());
        trainee.setActive(request.getIsActive());

        Trainee updated = traineeService.update(trainee);
        return ResponseEntity.ok(TraineeMapper.toProfile(updated));
    }

    @Operation(summary = "Delete trainee profile")
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteProfile(
            @PathVariable String username,
            @RequestAttribute(AuthenticationFilter.AUTHENTICATED_USERNAME_ATTR) String authenticated) {
        requireSelf(username, authenticated);
        traineeService.deleteByUsername(username);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get active trainers not assigned to trainee")
    @GetMapping("/{username}/trainers/not-assigned")
    public ResponseEntity<List<NotAssignedTrainerResponse>> getNotAssignedTrainers(
            @PathVariable String username,
            @RequestAttribute(AuthenticationFilter.AUTHENTICATED_USERNAME_ATTR) String authenticated) {
        requireSelf(username, authenticated);
        List<Trainer> trainers = trainerService.getTrainersNotAssignedToTrainee(username);
        return ResponseEntity.ok(trainers.stream().map(TrainerMapper::toNotAssigned).toList());
    }

    @Operation(summary = "Update trainee's trainer list")
    @PutMapping("/{username}/trainers")
    public ResponseEntity<List<TrainerSummaryResponse>> updateTrainers(
            @PathVariable String username,
            @RequestAttribute(AuthenticationFilter.AUTHENTICATED_USERNAME_ATTR) String authenticated,
            @Valid @RequestBody UpdateTraineeTrainersRequest request) {
        requireSelf(username, authenticated);

        List<String> trainerUsernames = request.getTrainers().stream()
                .map(TrainerUsernameRequest::getTrainerUsername)
                .toList();

        traineeService.updateTrainers(username, trainerUsernames);
        Trainee trainee = traineeService.findByUsername(username);

        List<TrainerSummaryResponse> response = trainee.getTrainers().stream()
                .map(TrainerMapper::toSummary)
                .toList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get trainee trainings with optional filters")
    @GetMapping("/{username}/trainings")
    public ResponseEntity<List<TraineeTrainingResponse>> getTrainings(
            @PathVariable String username,
            @RequestAttribute(AuthenticationFilter.AUTHENTICATED_USERNAME_ATTR) String authenticated,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) String trainerName,
            @RequestParam(required = false) TrainingTypeName trainingType) {
        requireSelf(username, authenticated);

        TrainingFilterDTO filter = new TrainingFilterDTO(username, fromDate, toDate, trainerName, trainingType);
        List<Training> trainings = trainingService.getTraineeTrainings(username, filter);

        return ResponseEntity.ok(trainings.stream().map(TrainingMapper::toTraineeTraining).toList());
    }

    @Operation(summary = "Activate or deactivate trainee")
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
