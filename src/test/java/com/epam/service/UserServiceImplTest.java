package com.epam.service;

import com.epam.domain.Trainee;
import com.epam.exception.AuthenticationException;
import com.epam.exception.UserNotFoundException;
import com.epam.exception.ValidationException;
import com.epam.repository.UserDao;
import com.epam.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserDao userDao;

    @Test
    public void shouldGeneratePasswordWithCorrectLength() {
        String password = userService.generatePassword();

        assertNotNull(password);
        assertEquals(10, password.length());
    }

    @Test
    public void shouldGenerateDifferentPasswords() {
        String firstPassword = userService.generatePassword();
        String secondPassword = userService.generatePassword();

        assertNotEquals(firstPassword, secondPassword);
    }

    @Test
    public void shouldGenerateBaseUsernameWhenUsernameDoesNotExist() {
        Trainee trainee = generateUser();
        when(userDao.existsByUsername("Bilbo.Baggins")).thenReturn(false);

        String result = userService.generateUsername(trainee);

        assertEquals("Bilbo.Baggins", result);
        verify(userDao).existsByUsername("Bilbo.Baggins");
    }

    @Test
    public void shouldGenerateUsernameWithSuffixWhenBaseUsernameExists() {
        Trainee trainee = generateUser();

        when(userDao.existsByUsername("Bilbo.Baggins")).thenReturn(true);
        when(userDao.existsByUsername("Bilbo.Baggins1")).thenReturn(false);

        String result = userService.generateUsername(trainee);

        assertEquals("Bilbo.Baggins1", result);
    }

    @Test
    public void shouldAddSuffixWhenUsernameTakenByOtherUser() {
        Trainee trainee = generateUser();

        when(userDao.existsByUsername("Bilbo.Baggins")).thenReturn(true);
        when(userDao.existsByUsername("Bilbo.Baggins1")).thenReturn(false);

        assertEquals("Bilbo.Baggins1", userService.generateUsername(trainee));
    }

    @Test
    public void shouldGenerateCredentialsAndPersist() {
        Trainee trainee = generateUser();
        when(userDao.existsByUsername("Bilbo.Baggins")).thenReturn(false);
        when(userDao.add(trainee)).thenReturn(trainee);

        Trainee result = (Trainee) userService.add(trainee);

        assertSame(trainee, result);
        assertEquals("Bilbo.Baggins", result.getUsername());
        assertNotNull(result.getPassword());
        verify(userDao).add(trainee);
    }

    @Test
    public void shouldThrowExceptionWhenFirstNameIsNull() {
        Trainee trainee = new Trainee();
        trainee.setLastName("Baggins");
        assertThrows(ValidationException.class, () -> userService.add(trainee));
        verifyNoInteractions(userDao);
    }

    @Test
    public void shouldThrowExceptionWhenLastNameIsBlank() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("Bilbo");
        trainee.setLastName("");
        assertThrows(ValidationException.class, () -> userService.add(trainee));
    }

    @Test
    public void shouldPreservePasswordAndIsActive() {
        Trainee existingTrainee = generateUser();
        existingTrainee.setId(1L);
        existingTrainee.setUsername("Bilbo.Baggins");
        existingTrainee.setPassword("password12");
        existingTrainee.setActive(true);

        Trainee traineeToUpdate = generateUser();
        traineeToUpdate.setId(1L);
        traineeToUpdate.setUsername("Bilbo.Baggins");
        traineeToUpdate.setPassword("password99");
        traineeToUpdate.setActive(false);

        when(userDao.findByUsername("Bilbo.Baggins")).thenReturn(Optional.of(existingTrainee));
        when(userDao.get(1L)).thenReturn(existingTrainee);
        when(userDao.update(any())).thenReturn(traineeToUpdate);

        userService.update("Bilbo.Baggins", "password12", traineeToUpdate);

        assertEquals("password12", traineeToUpdate.getPassword());
        assertTrue(traineeToUpdate.isActive());
    }

    @Test
    public void shouldThrowExceptionWhenIdIsNull() {
        Trainee auth = generateUser();
        auth.setPassword("password12");
        auth.setActive(true);
        when(userDao.findByUsername("Bilbo.Baggins")).thenReturn(Optional.of(auth));

        Trainee trainee = generateUser();
        assertThrows(UserNotFoundException.class, () -> userService.update("Bilbo.Baggins", "password12", trainee));
    }

    @Test
    public void shouldThrowWhenUsernameTakenByAnother() {
        Trainee auth = generateUser();
        auth.setId(1L);
        auth.setUsername("Bilbo.Baggins");
        auth.setPassword("password12");
        auth.setActive(true);
        when(userDao.findByUsername("Bilbo.Baggins")).thenReturn(Optional.of(auth));

        Trainee traineeToUpdate = generateUser();
        traineeToUpdate.setId(1L);
        traineeToUpdate.setUsername("Frodo.Baggins");

        when(userDao.get(1L)).thenReturn(auth);
        when(userDao.existsByUsername("Frodo.Baggins")).thenReturn(true);

        assertThrows(ValidationException.class, () -> userService.update("Bilbo.Baggins", "password12", traineeToUpdate));
    }

    @Test
    public void shouldAllowSameUsername() {
        Trainee existingTrainee = generateUser();
        existingTrainee.setId(1L);
        existingTrainee.setUsername("Bilbo.Baggins");
        existingTrainee.setPassword("password12");
        existingTrainee.setActive(true);

        when(userDao.findByUsername("Bilbo.Baggins")).thenReturn(Optional.of(existingTrainee));
        when(userDao.get(1L)).thenReturn(existingTrainee);
        when(userDao.update(any())).thenReturn(existingTrainee);

        userService.update("Bilbo.Baggins", "password12", existingTrainee);

        verify(userDao).update(existingTrainee);
    }

    @Test
    public void shouldFindByUsername() {
        Trainee trainee = generateUser();
        trainee.setPassword("password12");
        trainee.setActive(true);
        when(userDao.findByUsername("Bilbo.Baggins")).thenReturn(Optional.of(trainee));

        assertSame(trainee, userService.findByUsername("Bilbo.Baggins", "password12"));
    }

    @Test
    public void shouldThrowExceptionWhenTraineeNotFound() {
        when(userDao.findByUsername("Frodo.Baggins")).thenReturn(Optional.empty());
        assertThrows(AuthenticationException.class, () -> userService.findByUsername("Frodo.Baggins", "password12"));
    }

    @Test
    public void shouldUpdatePassword() {
        Trainee trainee = generateUser();
        trainee.setPassword("password12");
        trainee.setActive(true);
        when(userDao.findByUsername("Bilbo.Baggins")).thenReturn(Optional.of(trainee));

        userService.changePassword("Bilbo.Baggins", "password12", "password99");

        assertEquals("password99", trainee.getPassword());
    }

    @Test
    public void shouldActivate() {
        Trainee trainee = generateUser();
        trainee.setPassword("password12");
        trainee.setActive(false);
        when(userDao.findByUsername("Bilbo.Baggins")).thenReturn(Optional.of(trainee));

        userService.activate("Bilbo.Baggins", "password12");
        assertTrue(trainee.isActive());
    }

    @Test
    public void shouldThrowExceptionWhenTraineeIsActivated() {
        Trainee trainee = generateUser();
        trainee.setPassword("password12");
        trainee.setActive(true);
        when(userDao.findByUsername("Bilbo.Baggins")).thenReturn(Optional.of(trainee));

        assertThrows(ValidationException.class, () -> userService.activate("Bilbo.Baggins", "password12"));
        verify(userDao, never()).update(any());
    }

    @Test
    public void shouldDeactivate() {
        Trainee trainee = generateUser();
        trainee.setPassword("password12");
        trainee.setActive(true);
        when(userDao.findByUsername("Bilbo.Baggins")).thenReturn(Optional.of(trainee));

        userService.deactivate("Bilbo.Baggins", "password12");

        assertFalse(trainee.isActive());
        verify(userDao).update(trainee);
    }

    @Test
    public void shouldThrowExceptionWhenTraineeIsDeactivated() {
        Trainee trainee = generateUser();
        trainee.setPassword("password12");
        trainee.setActive(false);
        when(userDao.findByUsername("Bilbo.Baggins")).thenReturn(Optional.of(trainee));

        assertThrows(AuthenticationException.class, () -> userService.deactivate("Bilbo.Baggins", "password12"));
    }

    @Test
    public void shouldReturnTrueForValidCredentials() {
        Trainee trainee = generateUser();
        trainee.setPassword("password12");
        trainee.setActive(true);
        when(userDao.findByUsername("Bilbo.Baggins")).thenReturn(Optional.of(trainee));

        assertTrue(userService.matchCredentials("Bilbo.Baggins", "password12"));
    }

    @Test
    public void shouldReturnFalseForInvalidCredentials() {
        Trainee trainee = generateUser();
        trainee.setPassword("password12");
        trainee.setActive(true);
        when(userDao.findByUsername("Bilbo.Baggins")).thenReturn(Optional.of(trainee));

        assertFalse(userService.matchCredentials("Bilbo.Baggins", "password99"));
    }

    private Trainee generateUser() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("Bilbo");
        trainee.setLastName("Baggins");
        return trainee;
    }
}
