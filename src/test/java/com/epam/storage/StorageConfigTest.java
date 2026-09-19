package com.epam.storage;

import com.epam.facade.GymFacade;
import com.epam.object.Trainee;
import com.epam.object.Trainer;
import com.epam.object.Training;
import com.epam.service.TraineeService;
import com.epam.service.TrainerService;
import com.epam.service.TrainingService;
import com.epam.util.UsernameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class StorageConfigTest {

    @Test
    public void shouldInitializeSpringContext() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(StorageConfig.class)) {
            assertNotNull(context.getBean(TraineeService.class));
            assertNotNull(context.getBean(TrainerService.class));
            assertNotNull(context.getBean(TrainingService.class));
            assertNotNull(context.getBean(UsernameGenerator.class));
            assertNotNull(context.getBean(GymFacade.class));

            Map<Long, Trainee> traineeMap = context.getBean("traineeMap", Map.class);
            Map<Long, Trainer> trainerMap = context.getBean("trainerMap", Map.class);
            Map<Long, Training> trainingMap = context.getBean("trainingMap", Map.class);

            assertFalse(traineeMap.isEmpty());
            assertFalse(trainerMap.isEmpty());
            assertFalse(trainingMap.isEmpty());
        }
    }
}
