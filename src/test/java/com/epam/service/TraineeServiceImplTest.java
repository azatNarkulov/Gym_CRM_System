package com.epam.service;

import com.epam.domain.Trainee;
import com.epam.domain.Trainer;
import com.epam.exception.UserNotFoundException;
import com.epam.repository.TraineeDao;
import com.epam.service.impl.TraineeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TraineeServiceImplTest {

    @InjectMocks
    private TraineeServiceImpl traineeService;

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private UserService userService;

    @Test
    public void shouldDelegateAdd() {
        Trainee trainee = new Trainee();
        when(userService.add(trainee)).thenReturn(trainee);

        assertSame(trainee, traineeService.add(trainee));
        verify(userService).add(trainee);
    }

    @Test
    public void shouldDelegateUpdate() {
        Trainee trainee = new Trainee();
        when(userService.update("Bilbo.Baggins", "password12", trainee)).thenReturn(trainee);

        assertSame(trainee, traineeService.update("Bilbo.Baggins", "password12", trainee));
        verify(userService).update("Bilbo.Baggins", "password12", trainee);
    }

    @Test
    public void shouldDelegateFindByUsername() {
        Trainee trainee = new Trainee();
        when(userService.findByUsername("Bilbo.Baggins", "password12")).thenReturn(trainee);

        assertSame(trainee, traineeService.findByUsername("Bilbo.Baggins", "password12"));
        verify(userService).findByUsername("Bilbo.Baggins", "password12");
    }

    @Test
    public void shouldDelegateChangePassword() {
        traineeService.changePassword("Bilbo.Baggins", "password12", "password99");
        verify(userService).changePassword("Bilbo.Baggins", "password12", "password99");
    }

    @Test
    public void shouldDelegateActivate() {
        traineeService.activate("Bilbo.Baggins", "password12");
        verify(userService).activate("Bilbo.Baggins", "password12");
    }

    @Test
    public void shouldDelegateDeactivate() {
        traineeService.deactivate("Bilbo.Baggins", "password12");
        verify(userService).deactivate("Bilbo.Baggins", "password12");
    }

    @Test
    public void shouldDelegateMatchCredentials() {
        when(userService.matchCredentials("Bilbo.Baggins", "password12")).thenReturn(true);

        assertTrue(traineeService.matchCredentials("Bilbo.Baggins", "password12"));
        verify(userService).matchCredentials("Bilbo.Baggins", "password12");
    }

    @Test
    public void shouldDeleteByUsername() {
        when(traineeDao.delete("Bilbo.Baggins")).thenReturn(true);

        assertTrue(traineeService.deleteByUsername("Bilbo.Baggins", "password12"));

        verify(userService).checkCredentials("Bilbo.Baggins", "password12");
        verify(traineeDao).delete("Bilbo.Baggins");
    }

    @Test
    public void shouldUpdateTrainers() {
        Trainee trainee = generateTrainee();
        when(userService.findByUsernameInternal("Bilbo.Baggins")).thenReturn(trainee);

        Trainer trainer1 = new Trainer();
        trainer1.setUsername("Trainer1");
        Trainer trainer2 = new Trainer();
        trainer2.setUsername("Trainer2");

        when(userService.findByUsernameInternal("Trainer1")).thenReturn(trainer1);
        when(userService.findByUsernameInternal("Trainer2")).thenReturn(trainer2);

        traineeService.updateTrainers("Bilbo.Baggins", "password12", List.of("Trainer1", "Trainer2"));

        assertEquals(2, trainee.getTrainers().size());
    }

    @Test
    public void shouldThrowWhenTrainerNotFound() {
        Trainee trainee = generateTrainee();
        when(userService.findByUsernameInternal("Bilbo.Baggins")).thenReturn(trainee);
        when(userService.findByUsernameInternal("Trainer1")).thenThrow(new UserNotFoundException());

        assertThrows(UserNotFoundException.class,
                () -> traineeService.updateTrainers("Bilbo.Baggins", "password12", List.of("Trainer1")));
    }

    private Trainee generateTrainee() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("Bilbo");
        trainee.setLastName("Baggins");
        return trainee;
    }
}
