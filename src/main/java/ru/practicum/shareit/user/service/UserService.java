package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.user.validation.UserValidation;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;
    private final UserMapper userMapper;
    private final UserValidation userValidation;

    public UserDto create(UserCreateDto userCreateDto) {
        userValidation.validate(userCreateDto);
        return userMapper.toDto(userStorage.create(userMapper.toEntity(userCreateDto)));
    }

    public UserDto update(Long userId, UserUpdateDto userUpdateDto) {
        User oldUser = userStorage.getById(userId);
        if (userUpdateDto.getEmail() != null && !oldUser.getEmail().equalsIgnoreCase(userUpdateDto.getEmail())) {
            userValidation.validate(userUpdateDto);
        }

        if (userUpdateDto.getEmail() == null) {
            userUpdateDto.setEmail(oldUser.getEmail());
        }
        if (userUpdateDto.getName() == null) {
            userUpdateDto.setName(oldUser.getName());
        }

        return userMapper.toDto(userStorage.update(userId, userMapper.toEntity(userUpdateDto)));
    }

    public UserDto getById(Long userId) {
        return userMapper.toDto(userStorage.getById(userId));
    }

    public void delete(Long userId) {
        userStorage.delete(userId);
    }
}
