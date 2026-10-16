package com.epam.mapper;

import com.epam.domain.Trainee;
import com.epam.domain.Trainer;
import com.epam.domain.TrainingType;
import com.epam.domain.TrainingTypeName;
import com.epam.dto.response.NotAssignedTrainerResponse;
import com.epam.dto.response.TrainerProfileResponse;
import com.epam.dto.response.TrainerSummaryResponse;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TrainerMapperTest {

    @Test
    void shouldMapProfileWithTrainees() {
        Trainer trainer = generateTrainer();
        Trainee trainee = new Trainee();
        trainee.setUsername("Bilbo.Baggins");
        trainee.setFirstName("Bilbo");
        trainee.setLastName("Baggins");
        trainer.setTrainees(Set.of(trainee));

        TrainerProfileResponse response = TrainerMapper.toProfile(trainer);

        assertEquals("Gandalf.Grey", response.getUsername());
        assertEquals("Gandalf", response.getFirstName());
        assertEquals(1, response.getTrainees().size());
        assertEquals("Bilbo.Baggins", response.getTrainees().getFirst().getUsername());
    }

    @Test
    void shouldMapSummary() {
        TrainerSummaryResponse response = TrainerMapper.toSummary(generateTrainer());

        assertEquals("Gandalf.Grey", response.getUsername());
        assertEquals("FITNESS", response.getSpecialization().getName());
    }

    @Test
    void shouldMapNotAssigned() {
        NotAssignedTrainerResponse response = TrainerMapper.toNotAssigned(generateTrainer());

        assertEquals("Gandalf.Grey", response.getUsername());
        assertEquals("FITNESS", response.getSpecialization().getName());
    }

    private Trainer generateTrainer() {
        Trainer t = new Trainer();
        t.setUsername("Gandalf.Grey");
        t.setFirstName("Gandalf");
        t.setLastName("Grey");
        TrainingType type = new TrainingType();
        type.setName(TrainingTypeName.FITNESS);
        t.setSpecialization(type);
        return t;
    }
}
