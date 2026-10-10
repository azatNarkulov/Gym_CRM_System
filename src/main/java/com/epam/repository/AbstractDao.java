package com.epam.repository;

import com.epam.domain.BaseEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    protected abstract Class<T> getEntityClass();

    protected EntityManager getEntityManager() {
        return entityManager;
    }
}
