package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserService;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto create(@Valid @RequestBody UserCreateDto userCreateDto) {
        log.info("returning the created user {}", userCreateDto);
        return userService.create(userCreateDto);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable("userId") Long userId, @Valid @RequestBody UserUpdateDto userUpdateDto) {
        log.info("returning the updated user");
        return userService.update(userId, userUpdateDto);
    }

    @GetMapping("/{userId}")
    public UserDto getById(@Positive @PathVariable("userId") Long userId) {
        log.info("returning the user by ID {}", userId);
        return userService.getById(userId);
    }

    @DeleteMapping("/{userId}")
    public void  delete(@Positive @PathVariable("userId") Long userId) {
        log.info("deleting the user by ID {}", userId);
        userService.delete(userId);
    }

}
