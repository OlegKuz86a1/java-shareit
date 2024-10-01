package ru.practicum.shareit.user.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.common.exception.IllegalDataException;
import ru.practicum.shareit.user.dto.UserBaseGetter;
import ru.practicum.shareit.user.storage.UserStorage;

@Component
@RequiredArgsConstructor
public class UserValidation {

    private final UserStorage userStorage;

    public void validate(final UserBaseGetter user) {
        if (user.getEmail() != null && userStorage.existsByEmail(user.getEmail())) {
            throw new IllegalDataException("email already exists");
        }
    }
}
