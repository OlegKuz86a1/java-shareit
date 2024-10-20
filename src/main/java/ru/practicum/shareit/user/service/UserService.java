package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.exception.DuplicateException;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public UserDto create(UserCreateDto userCreateDto) {
        validate(userCreateDto.getEmail());
        return userMapper.toDto(userRepository.save(userMapper.toEntity(userCreateDto)));
    }

    public UserDto update(Long userId, UserUpdateDto userUpdateDto) {
        User oldUser = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id=%s not found", userId)));

        if (userUpdateDto.getEmail() != null && !oldUser.getEmail().equalsIgnoreCase(userUpdateDto.getEmail())) {
            validate(userUpdateDto.getEmail());
        }

        if (userUpdateDto.getEmail() == null) {
            userUpdateDto.setEmail(oldUser.getEmail());
        }
        if (userUpdateDto.getName() == null) {
            userUpdateDto.setName(oldUser.getName());
        }

        return userMapper.toDto(userRepository.save(userMapper.toEntity(userId, userUpdateDto)));
    }

    public UserDto getById(Long userId) {
        return userMapper.toDto(userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("User with id=%s not found", userId))));
    }

    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }

    private void validate(String email) {
        if (userRepository.existsUserByEmail(email))
            throw new DuplicateException(String.format("User with email=%s already exists", email));
    }
}
