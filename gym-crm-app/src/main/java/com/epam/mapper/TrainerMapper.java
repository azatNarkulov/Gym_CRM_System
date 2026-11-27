package com.epam.mapper;

import com.epam.domain.Trainee;
import com.epam.domain.Trainer;
import com.epam.dto.response.NotAssignedTrainerResponse;
import com.epam.dto.response.TraineeSummaryResponse;
import com.epam.dto.response.TrainerProfileResponse;
import com.epam.dto.response.TrainerSummaryResponse;

import java.util.List;

public class TrainerMapper {
    public static TrainerProfileResponse toProfile(Trainer trainer) {
        List<TraineeSummaryResponse> trainees = trainer.getTrainees().stream()
                .map(TrainerMapper::toTraineeSummary)
                .toList();

        return new TrainerProfileResponse(
                trainer.getUsername(),
                trainer.getFirstName(),
                trainer.getLastName(),
                TrainingTypeMapper.toResponse(trainer.getSpecialization()),
                trainer.isActive(),
                trainees);
    }

    public static TrainerSummaryResponse toSummary(Trainer trainer) {
        return new TrainerSummaryResponse(
                trainer.getUsername(),
                trainer.getFirstName(),
                trainer.getLastName(),
                TrainingTypeMapper.toResponse(trainer.getSpecialization()));
    }

    public static NotAssignedTrainerResponse toNotAssigned(Trainer trainer) {
        return new NotAssignedTrainerResponse(
                trainer.getUsername(),
                trainer.getFirstName(),
                trainer.getLastName(),
                TrainingTypeMapper.toResponse(trainer.getSpecialization()));
    }

    private static TraineeSummaryResponse toTraineeSummary(Trainee trainee) {
        return new TraineeSummaryResponse(
                trainee.getUsername(),
                trainee.getFirstName(),
                trainee.getLastName());
    }
}
