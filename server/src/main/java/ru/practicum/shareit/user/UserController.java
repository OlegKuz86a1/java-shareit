package ru.practicum.shareit.user;

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
    public UserDto create(@RequestBody UserCreateDto userCreateDto) {
        UserDto userDto = userService.create(userCreateDto);
        log.info("added user {}", userDto);
        return userDto;
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable Long userId, @RequestBody UserUpdateDto userUpdateDto) {
        UserDto update = userService.update(userId, userUpdateDto);
        log.info("updated user {}", update);
        return update;
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable Long userId) {
        UserDto byId = userService.getById(userId);
        log.info("get user by ID {}", byId);
        return byId;
    }

    @DeleteMapping("/{userId}")
    public void  delete(@PathVariable Long userId) {
        userService.delete(userId);
    }

}
