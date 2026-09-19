package com.epam.storage;

import com.epam.util.TrainingType;
import com.epam.object.Trainee;
import com.epam.object.Trainer;
import com.epam.object.Training;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class StorageInitializer implements BeanPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(StorageInitializer.class);

    @Value("${trainee.data.file.path}")
    private String traineeDataFilePath;

    @Value("${trainer.data.file.path}")
    private String trainerDataFilePath;

    @Value("${training.data.file.path}")
    private String trainingDataFilePath;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        switch (beanName) {
            case "traineeMap":
                log.info("Initializing trainee storage");
                Map<Long, Trainee> traineeMap = (Map<Long, Trainee>) bean;

                try {
                    List<String> lines = Files.readAllLines(Paths.get(traineeDataFilePath));
                    for (String line : lines) {
                        String[] fields = line.split(";");
                        Trainee trainee = new Trainee();
                        trainee.setFirstName(fields[0]);
                        trainee.setLastName(fields[1]);
                        trainee.setUsername(fields[2]);
                        trainee.setPassword(fields[3]);
                        trainee.setActive(Boolean.parseBoolean(fields[4]));
                        trainee.setDateOfBirth(LocalDate.parse(fields[5]));
                        trainee.setAddress(fields[6]);
                        trainee.setUserId(Long.valueOf(fields[7]));
                        traineeMap.put(trainee.getUserId(), trainee);
                    }

                    log.info("Trainee storage initialized: {} records", traineeMap.size());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                break;
            case "trainerMap":
                log.info("Initializing trainer storage");
                Map<Long, Trainer> trainerMap = (Map<Long, Trainer>) bean;

                try {
                    List<String> lines = Files.readAllLines(Paths.get(trainerDataFilePath));
                    for (String line : lines) {
                        String[] fields = line.split(";");
                        Trainer trainer = new Trainer();
                        trainer.setFirstName(fields[0]);
                        trainer.setLastName(fields[1]);
                        trainer.setUsername(fields[2]);
                        trainer.setPassword(fields[3]);
                        trainer.setActive(Boolean.parseBoolean(fields[4]));
                        trainer.setSpecialization(TrainingType.valueOf(fields[5]));
                        trainer.setUserId(Long.valueOf(fields[6]));
                        trainerMap.put(trainer.getUserId(), trainer);
                    }

                    log.info("Trainer storage initialized: {} records", trainerMap.size());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                break;
            case "trainingMap":
                log.info("Initializing training storage");
                Map<Long, Training> trainingMap = (Map<Long, Training>) bean;

                try {
                    List<String> lines = Files.readAllLines(Paths.get(trainingDataFilePath));
                    for (String line : lines) {
                        String[] fields = line.split(";");
                        Training training = new Training();
                        training.setTraineeId(Long.valueOf(fields[0]));
                        training.setTrainerId(Long.valueOf(fields[1]));
                        training.setTrainingName(fields[2]);
                        training.setTrainingType(TrainingType.valueOf(fields[3]));
                        training.setTrainingDate(LocalDate.parse(fields[4]));
                        training.setTrainingDuration(Duration.parse(fields[5]));
                        training.setTrainingId(Long.valueOf(fields[6]));
                        trainingMap.put(training.getTrainingId(), training);
                    }

                    log.info("Training storage initialized: {} records", trainingMap.size());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                break;
            default:
                break;
        }

        return bean;
    }
}
