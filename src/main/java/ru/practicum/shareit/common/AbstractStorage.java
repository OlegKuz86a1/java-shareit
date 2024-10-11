package ru.practicum.shareit.common;

import ru.practicum.shareit.common.exception.NotFoundException;

import java.util.*;

public abstract class AbstractStorage<T> {

    protected final Map<Long, T> storage = new HashMap<>();

    public List<T> getAll() {
        return new ArrayList<>(storage.values());
    }

    public T getById(long id) {
        checkExists(id);
        return storage.get(id);
    }

    public T create(T entity) {
        Long id = generateId();

        if (entity instanceof BaseEntity) {
            ((BaseEntity) entity).setId(id);
        }

        storage.put(id, entity);
        return entity;
    }

    public void delete(long id) {
        checkExists(id);
        storage.remove(id);
    }

    public T update(long id, T entity) {
        checkExists(id);

        if (entity instanceof BaseEntity) {
            ((BaseEntity) entity).setId(id);
        }

        storage.put(id, entity);
        return entity;
    }

    private Long generateId() {
        return storage.keySet().stream().max(Long::compare).orElse(0L) + 1;
    }

    private void checkExists(Long id) {
        if (id == null || !storage.containsKey(id)) {
            throw new NotFoundException("Not found entity with id " + id);
        }
    }
}
