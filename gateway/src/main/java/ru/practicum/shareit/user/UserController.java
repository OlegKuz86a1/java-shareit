package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;


@Slf4j
@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserCreateDto userCreateDto) {
        log.info("request for additions user {}", userCreateDto);
        return userClient.create(userCreateDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@Positive @PathVariable Long userId, @Valid @RequestBody UserUpdateDto userUpdateDto) {
        log.info("request for updated by user id {} and new data{}", userId, userUpdateDto);

        return userClient.update(userId, userUpdateDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getById(@Positive @PathVariable Long userId) {
        log.info("request for getting user by ID = {}", userId);
        return userClient.getById(userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@Positive @PathVariable Long userId) {
        log.info("request for deleting the user by ID {}", userId);
        return userClient.delete(userId);
    }

}
