package ru.practicum.shareit.user.storage;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.common.AbstractStorage;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;

@Repository
public class UserStorageImpl extends AbstractStorage<User> implements UserStorage {

    private Map<String, User> emailIndex;

    @PostConstruct
    public void init() {
        this.emailIndex = new HashMap<>();
        getAll().forEach(user -> emailIndex.put(user.getEmail().toLowerCase(), user));
    }

    @Override
    public boolean existsByEmail(String email) {
        String lowercaseEmail = email.toLowerCase();
        return emailIndex.containsKey(lowercaseEmail);
    }
}
