package com.epam.repository;

import com.epam.domain.BaseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public abstract class AbstractDao<T extends BaseEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractDao.class);

    protected Map<Long, T> storage;

    protected void setStorage(Map<Long, T> storage) {
        this.storage = storage;
    }

    public T add(T entity) {
        Long id = generateId();
        entity.setId(id);
        storage.put(id, entity);

        LOG.info("Adding entity with id={}", id);
        return entity;
    }

    public T get(Long id) {
        LOG.debug("Searching for entity with id={}", id);
        return storage.get(id);
    }

    protected Long generateId() {
        return storage.keySet().stream()
                .max(Long::compareTo)
                .orElse(0L) + 1;
    }
}
