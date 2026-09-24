package com.epam.storage;

import com.epam.domain.Trainee;
import com.epam.domain.Trainer;
import com.epam.domain.Training;
import com.epam.domain.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StorageInitializerTest {

    private Map<Long, Trainee> traineeMap;
    private Map<Long, Trainer> trainerMap;
    private Map<Long, Training> trainingMap;

    private StorageInitializer storageInitializer;

    @BeforeEach
    public void setUp() {
        traineeMap = new HashMap<>();
        trainerMap = new HashMap<>();
        trainingMap = new HashMap<>();

        storageInitializer = new StorageInitializer();
        storageInitializer.setTraineeMap(traineeMap);
        storageInitializer.setTrainerMap(trainerMap);
        storageInitializer.setTrainingMap(trainingMap);
    }

    @Test
    public void shouldInitializeTrainees(@TempDir Path tempDir) throws Exception {
        Path file = createFile(tempDir, "trainee.csv", "Ivan;Trainee;Ivan.Trainee;password12;true;2000-12-14;Moscow Street 191;1");

        setField("traineeDataFilePath", file.toString());
        setField("trainerDataFilePath", createFile(tempDir, "trainer.csv", "").toString());
        setField("trainingDataFilePath", createFile(tempDir, "training.csv", "").toString());

        storageInitializer.initialize();

        assertEquals(1, traineeMap.size());

        Trainee trainee = traineeMap.get(1L);

        assertNotNull(trainee);
        assertEquals("Ivan", trainee.getFirstName());
        assertEquals("Trainee", trainee.getLastName());
        assertEquals("Ivan.Trainee", trainee.getUsername());
        assertEquals("password12", trainee.getPassword());
        assertTrue(trainee.isActive());
        assertEquals(LocalDate.of(2000, 12, 14), trainee.getDateOfBirth());
        assertEquals("Moscow Street 191", trainee.getAddress());
        assertEquals(1L, trainee.getId());
    }

    @Test
    public void shouldInitializeTrainers(@TempDir Path tempDir) throws Exception {
        Path file = createFile(tempDir, "trainer.csv", "Remy;Trainer;Remy.Trainer;password23;true;STRETCHING;1");

        setField("traineeDataFilePath", createFile(tempDir, "trainee.csv", "").toString());
        setField("trainerDataFilePath", file.toString());
        setField("trainingDataFilePath", createFile(tempDir, "training.csv", "").toString());

        storageInitializer.initialize();

        assertEquals(1, trainerMap.size());

        Trainer trainer = trainerMap.get(1L);

        assertNotNull(trainer);
        assertEquals("Remy", trainer.getFirstName());
        assertEquals("Trainer", trainer.getLastName());
        assertEquals("Remy.Trainer", trainer.getUsername());
        assertEquals("password23", trainer.getPassword());
        assertTrue(trainer.isActive());
        assertEquals("STRETCHING", trainer.getSpecialization().name());
        assertEquals(1L, trainer.getId());
    }

    @Test
    public void shouldInitializeTrainings(@TempDir Path tempDir) throws Exception {
        Path file = createFile(tempDir, "training.csv", "1;1;Saturday Stretching;STRETCHING;2026-09-19;PT1H;1");

        setField("traineeDataFilePath", createFile(tempDir, "trainee.csv", "").toString());
        setField("trainerDataFilePath", createFile(tempDir, "trainer.csv", "").toString());
        setField("trainingDataFilePath", file.toString());

        storageInitializer.initialize();

        assertEquals(1, trainingMap.size());

        Training training = trainingMap.get(1L);

        assertNotNull(training);
        assertEquals(1L, training.getTraineeId());
        assertEquals(1L, training.getTrainerId());
        assertEquals("Saturday Stretching", training.getTrainingName());
        assertEquals(TrainingType.STRETCHING, training.getTrainingType());
        assertEquals(LocalDate.of(2026, 9, 19), training.getTrainingDate());
        assertEquals(Duration.ofHours(1), training.getTrainingDuration());
        assertEquals(1L, training.getId());
    }

    private Path createFile(Path tempDir, String fileName, String content) throws IOException {
        Path file = tempDir.resolve(fileName);
        Files.write(file, content.getBytes());
        return file;
    }

    private void setField(String fieldName, String value) throws Exception {
        Field field = StorageInitializer.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(storageInitializer, value);
    }
}
