package com.epam.mapper;

import com.epam.domain.Trainee;
import com.epam.dto.response.TraineeProfileResponse;
import com.epam.dto.response.TrainerSummaryResponse;

import java.util.List;

public class TraineeMapper {
    public static TraineeProfileResponse toProfile(Trainee trainee) {
        List<TrainerSummaryResponse> trainers = trainee.getTrainers().stream()
                .map(TrainerMapper::toSummary)
                .toList();

        return new TraineeProfileResponse(
                trainee.getUsername(),
                trainee.getFirstName(),
                trainee.getLastName(),
                trainee.getDateOfBirth(),
                trainee.getAddress(),
                trainee.isActive(),
                trainers);
    }
}
