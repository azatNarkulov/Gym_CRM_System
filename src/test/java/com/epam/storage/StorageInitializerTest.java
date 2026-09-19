package com.epam.storage;

import com.epam.object.Trainee;
import com.epam.object.Trainer;
import com.epam.object.Training;
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

    private StorageInitializer storageInitializer;

    @BeforeEach
    public void setUp() {
        storageInitializer = new StorageInitializer();
    }

    @Test
    public void shouldInitializeTraineeMap(@TempDir Path tempDir) throws Exception {
        Path file = createFile(tempDir, "Ivan;Trainee;Ivan.Trainee;password12;true;2000-12-14;Moscow Street 191;1");

        setField("traineeDataFilePath", file.toString());

        Map<Long, Trainee> traineeMap = new HashMap<>();

        storageInitializer.postProcessAfterInitialization(traineeMap, "traineeMap");

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
        assertEquals(1L, trainee.getUserId());
    }

    @Test
    public void shouldInitializeTrainerMap(@TempDir Path tempDir) throws Exception {
        Path file = createFile(tempDir, "Remy;Trainer;Remy.Trainer;password23;true;STRETCHING;1");

        setField("trainerDataFilePath", file.toString());

        Map<Long, Trainer> trainerMap = new HashMap<>();

        storageInitializer.postProcessAfterInitialization(trainerMap, "trainerMap");

        assertEquals(1, trainerMap.size());

        Trainer trainer = trainerMap.get(1L);

        assertNotNull(trainer);
        assertEquals("Remy", trainer.getFirstName());
        assertEquals("Trainer", trainer.getLastName());
        assertEquals("Remy.Trainer", trainer.getUsername());
        assertEquals("password23", trainer.getPassword());
        assertTrue(trainer.isActive());
        assertEquals("STRETCHING", trainer.getSpecialization().name());
        assertEquals(1L, trainer.getUserId());
    }

    @Test
    public void shouldInitializeTrainingMap(@TempDir Path tempDir) throws Exception {
        Path file = createFile(tempDir, "1;1;Saturday Stretching;STRETCHING;2026-09-19;PT1H;1");

        setField("trainingDataFilePath", file.toString());

        Map<Long, Training> trainingMap = new HashMap<>();

        storageInitializer.postProcessAfterInitialization(trainingMap, "trainingMap");

        assertEquals(1, trainingMap.size());

        Training training = trainingMap.get(1L);

        assertNotNull(training);
        assertEquals(1L, training.getTraineeId());
        assertEquals(1L, training.getTrainerId());
        assertEquals("Saturday Stretching", training.getTrainingName());
        assertEquals("STRETCHING", training.getTrainingType().name());
        assertEquals(LocalDate.of(2026, 9, 19), training.getTrainingDate());
        assertEquals(Duration.ofHours(1), training.getTrainingDuration());
        assertEquals(1L, training.getTrainingId());
    }

    private Path createFile(Path tempDir, String content) throws IOException {
        Path file = tempDir.resolve("data.csv");
        Files.write(file, content.getBytes());
        return file;
    }

    private void setField(String fieldName, String value) throws Exception {
        Field field = StorageInitializer.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(storageInitializer, value);
    }
}
