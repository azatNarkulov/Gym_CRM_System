package com.epam.repository;

import com.epam.domain.BaseEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class AbstractDao<T extends BaseEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractDao.class);

    @PersistenceContext
    private EntityManager entityManager;

    public T add(T entity) {
        entityManager.persist(entity);
        LOG.debug("Adding entity with id={}", entity.getId());
        return entity;
    }

    public T get(Long id) {
        LOG.debug("Searching for entity with id={}", id);
        return entityManager.find(getEntityClass(), id);
    }

    public List<T> findAll() {
        LOG.debug("Finding all entities of type {}", getEntityClass().getSimpleName());
        String query = "SELECT e FROM " + getEntityClass().getSimpleName() + " e";
        return entityManager.createQuery(query, getEntityClass()).getResultList();
    }

    protected abstract Class<T> getEntityClass();

    protected EntityManager getEntityManager() {
        return entityManager;
    }
}
