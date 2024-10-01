package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.common.AbstractStorage;
import ru.practicum.shareit.user.model.User;

@Repository
public class UserStorageImpl extends AbstractStorage<User> implements UserStorage {

    @Override
    public boolean existsByEmail(String email) {
        return getAll().stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }
}
