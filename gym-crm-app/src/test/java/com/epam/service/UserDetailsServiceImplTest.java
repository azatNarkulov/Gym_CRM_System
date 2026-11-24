package com.epam.service;

import com.epam.domain.Trainee;
import com.epam.domain.Trainer;
import com.epam.exception.UserNotFoundException;
import com.epam.service.impl.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTest {

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private UserService userService;

    @Test
    public void shouldLoadTraineeWithRoleAuthority() {
        Trainee trainee = generateTrainee();
        when(userService.findByUsername(trainee.getUsername())).thenReturn(trainee);

        UserDetails details = userDetailsService.loadUserByUsername(trainee.getUsername());

        assertEquals(trainee.getUsername(), details.getUsername());
        assertEquals(trainee.getPassword(), details.getPassword());
        assertTrue(details.isEnabled());
        assertTrue(details.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_TRAINEE")));
    }

    @Test
    public void shouldLoadTrainerWithRoleAuthority() {
        Trainer trainer = generateTrainer();
        when(userService.findByUsername(trainer.getUsername())).thenReturn(trainer);

        UserDetails details = userDetailsService.loadUserByUsername(trainer.getUsername());

        assertTrue(details.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_TRAINER")));
    }

    @Test
    public void shouldMarkInactiveUserAsDisabled() {
        Trainee trainee = generateTrainee();
        trainee.setActive(false);
        when(userService.findByUsername(trainee.getUsername())).thenReturn(trainee);

        UserDetails details = userDetailsService.loadUserByUsername(trainee.getUsername());

        assertFalse(details.isEnabled());
    }

    @Test
    public void shouldTranslateUserNotFoundToUsernameNotFound() {
        when(userService.findByUsername("Frodo.Baggins")).thenThrow(new UserNotFoundException("Frodo.Baggins"));

        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("Frodo.Baggins"));
    }

    private Trainee generateTrainee() {
        Trainee trainee = new Trainee();
        trainee.setUsername("Bilbo.Baggins");
        trainee.setPassword("$2a$10$hash");
        trainee.setActive(true);
        return trainee;
    }

    private Trainer generateTrainer() {
        Trainer trainer = new Trainer();
        trainer.setUsername("Gandalf.Grey");
        trainer.setPassword("$2a$10$hash");
        trainer.setActive(true);
        return trainer;
    }
}
