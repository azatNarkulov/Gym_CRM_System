package com.epam.repository;

import com.epam.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserDaoTest {

    @InjectMocks
    private UserDao userDao;

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<User> userQuery;

    @Mock
    private TypedQuery<Integer> intQuery;

    @Test
    public void shouldPersistAndReturnEntity() {
        User user = new User();

        User result = userDao.add(user);

        assertSame(user, result);
        verify(entityManager).persist(user);
    }

    @Test
    public void shouldMerge() {
        User user = new User();
        when(entityManager.merge(user)).thenReturn(user);

        assertSame(user, userDao.update(user));
        verify(entityManager).merge(user);
    }

    @Test
    public void shouldReturnTrueWhenExistsByUsername() {
        when(entityManager.createQuery(anyString(), eq(Integer.class))).thenReturn(intQuery);
        when(intQuery.setParameter(anyString(), any())).thenReturn(intQuery);
        when(intQuery.setMaxResults(1)).thenReturn(intQuery);
        when(intQuery.getResultList()).thenReturn(List.of(1));

        assertTrue(userDao.existsByUsername("Bilbo.Baggins"));
    }

    @Test
    public void shouldReturnFalseWhenDoesntExistsByUsername() {
        when(entityManager.createQuery(anyString(), eq(Integer.class))).thenReturn(intQuery);
        when(intQuery.setParameter(anyString(), any())).thenReturn(intQuery);
        when(intQuery.setMaxResults(1)).thenReturn(intQuery);
        when(intQuery.getResultList()).thenReturn(List.of());

        assertFalse(userDao.existsByUsername("Frodo.Baggins"));
    }

    @Test
    public void shouldFindByUsername() {
        User user = new User();
        when(entityManager.createQuery(anyString(), eq(User.class))).thenReturn(userQuery);
        when(userQuery.setParameter(anyString(), any())).thenReturn(userQuery);
        when(userQuery.getResultStream()).thenReturn(Stream.of(user));

        assertTrue(userDao.findByUsername("Bilbo.Baggins").isPresent());
    }

    @Test
    public void shouldReturnEmpty() {
        when(entityManager.createQuery(anyString(), eq(User.class))).thenReturn(userQuery);
        when(userQuery.setParameter(anyString(), any())).thenReturn(userQuery);
        when(userQuery.getResultStream()).thenReturn(Stream.empty());

        assertTrue(userDao.findByUsername("Frodo.Baggins").isEmpty());
    }

    @Test
    public void shouldGetUserById() {
        User user = new User();
        when(entityManager.find(User.class, 1L)).thenReturn(user);

        assertSame(user, userDao.get(1L));
        verify(entityManager).find(User.class, 1L);
    }
}
