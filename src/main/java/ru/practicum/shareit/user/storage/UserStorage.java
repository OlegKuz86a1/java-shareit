package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

public interface UserStorage {

    User create(User user);

    User update(long id, User user);

    void delete(long id);

    User getById(long id);

    boolean existsByEmail(String email);
}
