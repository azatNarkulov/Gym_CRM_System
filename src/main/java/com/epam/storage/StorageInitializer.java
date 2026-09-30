package com.epam.storage;

import com.epam.exception.StorageInitializerException;
import com.epam.domain.TrainingType;
import com.epam.domain.Trainee;
import com.epam.domain.Trainer;
import com.epam.domain.Training;
import com.epam.repository.TraineeDao;
import com.epam.repository.TrainerDao;
import com.epam.repository.TrainingDao;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Component
public class StorageInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(StorageInitializer.class);

    private static final String SEPARATOR = ";";

    private String traineeDataFilePath;
    private String trainerDataFilePath;
    private String trainingDataFilePath;

    private TraineeDao traineeDao;
    private TrainerDao trainerDao;
    private TrainingDao trainingDao;

    @Value("${trainee.data.file.path}")
    public void setTraineeDataFilePath(String traineeDataFilePath) {
        this.traineeDataFilePath = traineeDataFilePath;
    }

    @Value("${trainer.data.file.path}")
    public void setTrainerDataFilePath(String trainerDataFilePath) {
        this.trainerDataFilePath = trainerDataFilePath;
    }

    @Value("${training.data.file.path}")
    public void setTrainingDataFilePath(String trainingDataFilePath) {
        this.trainingDataFilePath = trainingDataFilePath;
    }

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Autowired
    public void setTrainingDao(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }

    @PostConstruct
    public void initialize() {
        initializeTrainees();
        initializeTrainers();
        initializeTrainings();
    }

    private void initializeTrainees() {
        LOG.info("Initializing trainee storage");

        try {
            List<String> lines = Files.readAllLines(Paths.get(traineeDataFilePath));

            for (String line : lines) {
                String[] fields = line.split(SEPARATOR);

                Trainee trainee = new Trainee();
                trainee.setFirstName(fields[0]);
                trainee.setLastName(fields[1]);
                trainee.setUsername(fields[2]);
                trainee.setPassword(fields[3]);
                trainee.setActive(Boolean.parseBoolean(fields[4]));
                trainee.setDateOfBirth(LocalDate.parse(fields[5]));
                trainee.setAddress(fields[6]);
                trainee.setId(Long.valueOf(fields[7]));

                traineeDao.load(trainee);
            }

            LOG.info("Trainee storage initialized");
        } catch (IOException e) {
            throw new StorageInitializerException("Failed to initialize trainee storage", e);
        }
    }

    private void initializeTrainers() {
        LOG.info("Initializing trainer storage");

        try {
            List<String> lines = Files.readAllLines(Paths.get(trainerDataFilePath));

            for (String line : lines) {
                String[] fields = line.split(SEPARATOR);

                Trainer trainer = new Trainer();
                trainer.setFirstName(fields[0]);
                trainer.setLastName(fields[1]);
                trainer.setUsername(fields[2]);
                trainer.setPassword(fields[3]);
                trainer.setActive(Boolean.parseBoolean(fields[4]));
                trainer.setSpecialization(TrainingType.valueOf(fields[5]));
                trainer.setId(Long.valueOf(fields[6]));

                trainerDao.load(trainer);
            }

            LOG.info("Trainer storage initialized");
        } catch (IOException e) {
            throw new StorageInitializerException("Failed to initialize trainer storage", e);
        }
    }

    private void initializeTrainings() {
        LOG.info("Initializing training storage");

        try {
            List<String> lines = Files.readAllLines(Paths.get(trainingDataFilePath));

            for (String line : lines) {
                String[] fields = line.split(SEPARATOR);
                Training training = new Training();
                training.setTraineeId(Long.valueOf(fields[0]));
                training.setTrainerId(Long.valueOf(fields[1]));
                training.setTrainingName(fields[2]);
                training.setTrainingType(TrainingType.valueOf(fields[3]));
                training.setTrainingDate(LocalDate.parse(fields[4]));
                training.setTrainingDuration(Duration.parse(fields[5]));
                training.setId(Long.valueOf(fields[6]));

                trainingDao.load(training);
            }

            LOG.info("Training storage initialized");
        } catch (IOException e) {
            throw new StorageInitializerException("Failed to initialize training storage", e);
        }
    }
}
