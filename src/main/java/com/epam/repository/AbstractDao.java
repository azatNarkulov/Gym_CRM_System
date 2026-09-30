package com.epam.repository;

import com.epam.domain.BaseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public abstract class AbstractDao<T extends BaseEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractDao.class);

    protected abstract Map<Long, T> getStorage();

    public T add(T entity) {
        Long id = generateId();
        entity.setId(id);

        getStorage().put(id, entity);

        LOG.debug("Adding entity with id={}", id);
        return entity;
    }

    public T get(Long id) {
        LOG.debug("Searching for entity with id={}", id);
        return getStorage().get(id);
    }

    public T load(T entity) {
        getStorage().put(entity.getId(), entity);

        LOG.debug("Loading entity with id={}", entity.getId());
        return entity;
    }

    protected Long generateId() {
        return getStorage().keySet().stream()
                .max(Long::compareTo)
                .orElse(0L) + 1;
    }
}
