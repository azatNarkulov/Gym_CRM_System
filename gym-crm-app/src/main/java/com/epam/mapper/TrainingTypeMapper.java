package com.epam.mapper;

import com.epam.domain.TrainingType;
import com.epam.dto.response.TrainingTypeResponse;

public class TrainingTypeMapper {

    public static TrainingTypeResponse toResponse(TrainingType type) {
        if (type == null) {
            return new TrainingTypeResponse(null, null);
        }
        return new TrainingTypeResponse(type.getId(), type.getName().name());
    }
}
