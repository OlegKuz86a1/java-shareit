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
        log.info("request for additions user {}", userCreateDto);
        UserDto userDto = userService.create(userCreateDto);
        log.info("added user {}", userDto);
        return userDto;
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable Long userId, @Valid @RequestBody UserUpdateDto userUpdateDto) {
        log.info("request for updated user{}", userUpdateDto);
        UserDto update = userService.update(userId, userUpdateDto);
        log.info("updated user {}", update);
        return update;
    }

    @GetMapping("/{userId}")
    public UserDto getById(@Positive @PathVariable Long userId) {
        log.info("request for getting user by ID = {}", userId);
        UserDto byId = userService.getById(userId);
        log.info("get user by ID {}", byId);
        return byId;
    }

    @DeleteMapping("/{userId}")
    public void  delete(@Positive @PathVariable Long userId) {
        log.info("request for deleting the user by ID {}", userId);
        userService.delete(userId);
    }

}
