package com.epam.service;

import com.epam.domain.Trainee;
import com.epam.exception.UserNotFoundException;
import com.epam.exception.ValidationException;
import com.epam.repository.UserDao;
import com.epam.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

    @Mock
    private PasswordEncoder passwordEncoder;

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
    public void shouldPrepareCredentialsForUserCreation() {
        Trainee trainee = generateUser();
        when(userDao.existsByUsername("Bilbo.Baggins")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashed");

        userService.prepareForCreation(trainee);

        assertEquals("Bilbo.Baggins", trainee.getUsername());
        assertEquals("$2a$10$hashed", trainee.getPassword());
    }

    @Test
    public void shouldThrowExceptionWhenFirstNameIsNull() {
        Trainee trainee = new Trainee();
        trainee.setLastName("Baggins");
        assertThrows(ValidationException.class, () -> userService.prepareForCreation(trainee));
        verifyNoInteractions(userDao);
    }

    @Test
    public void shouldThrowExceptionWhenLastNameIsBlank() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("Bilbo");
        trainee.setLastName("");
        assertThrows(ValidationException.class, () -> userService.prepareForCreation(trainee));
    }

    @Test
    public void shouldUpdateIsActiveAndPreservePassword() {
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

        when(userDao.get(1L)).thenReturn(existingTrainee);
        when(userDao.update(any())).thenReturn(traineeToUpdate);

        userService.update(traineeToUpdate);

        assertEquals("password12", traineeToUpdate.getPassword());
        assertFalse(traineeToUpdate.isActive());
    }

    @Test
    public void shouldThrowExceptionWhenIdIsNull() {
        Trainee trainee = generateUser();
        assertThrows(UserNotFoundException.class, () -> userService.update(trainee));
    }

    @Test
    public void shouldThrowWhenTryingToUpdateUsername() {
        Trainee existing = generateUser();
        existing.setId(1L);
        existing.setUsername("Bilbo.Baggins");
        existing.setPassword("password12");
        existing.setActive(true);

        Trainee traineeToUpdate = generateUser();
        traineeToUpdate.setId(1L);
        traineeToUpdate.setUsername("Frodo.Baggins");

        when(userDao.get(1L)).thenReturn(existing);

        assertThrows(ValidationException.class, () -> userService.update(traineeToUpdate));
    }

    @Test
    public void shouldAllowSameUsername() {
        Trainee existingTrainee = generateUser();
        existingTrainee.setId(1L);
        existingTrainee.setUsername("Bilbo.Baggins");
        existingTrainee.setPassword("password12");
        existingTrainee.setActive(true);

        when(userDao.get(1L)).thenReturn(existingTrainee);
        when(userDao.update(any())).thenReturn(existingTrainee);

        userService.update(existingTrainee);

        verify(userDao).update(existingTrainee);
    }

    @Test
    public void shouldFindByUsername() {
        Trainee trainee = generateUser();
        when(userDao.findByUsername("Bilbo.Baggins")).thenReturn(Optional.of(trainee));

        assertSame(trainee, userService.findByUsername("Bilbo.Baggins"));
    }

    @Test
    public void shouldThrowExceptionWhenUserNotFound() {
        when(userDao.findByUsername("Frodo.Baggins")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.findByUsername("Frodo.Baggins"));
    }

    @Test
    public void shouldUpdatePassword() {
        Trainee trainee = generateUser();
        trainee.setPassword("$2a$10$oldhash");
        when(userDao.findByUsername("Bilbo.Baggins")).thenReturn(Optional.of(trainee));
        when(passwordEncoder.matches("password12", "$2a$10$oldhash")).thenReturn(true);
        when(passwordEncoder.encode("password99")).thenReturn("$2a$10$newhash");

        userService.changePassword("Bilbo.Baggins", "password12", "password99");

        assertEquals("$2a$10$newhash", trainee.getPassword());
    }

    @Test
    public void shouldActivate() {
        Trainee trainee = generateUser();
        trainee.setActive(false);
        when(userDao.findByUsername("Bilbo.Baggins")).thenReturn(Optional.of(trainee));

        userService.setActive("Bilbo.Baggins", true);
        assertTrue(trainee.isActive());
    }

    @Test
    public void shouldThrowExceptionWhenUserAlreadyActive() {
        Trainee trainee = generateUser();
        trainee.setActive(true);
        when(userDao.findByUsername("Bilbo.Baggins")).thenReturn(Optional.of(trainee));

        assertThrows(ValidationException.class, () -> userService.setActive("Bilbo.Baggins", true));
        verify(userDao, never()).update(any());
    }

    @Test
    public void shouldDeactivate() {
        Trainee trainee = generateUser();
        trainee.setActive(true);
        when(userDao.findByUsername("Bilbo.Baggins")).thenReturn(Optional.of(trainee));

        userService.setActive("Bilbo.Baggins", false);

        assertFalse(trainee.isActive());
        verify(userDao).update(trainee);
    }

    @Test
    public void shouldThrowExceptionWhenUserAlreadyDeactivated() {
        Trainee trainee = generateUser();
        trainee.setActive(false);
        when(userDao.findByUsername("Bilbo.Baggins")).thenReturn(Optional.of(trainee));

        assertThrows(ValidationException.class, () -> userService.setActive("Bilbo.Baggins", false));
    }

    private Trainee generateUser() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("Bilbo");
        trainee.setLastName("Baggins");
        return trainee;
    }
}
