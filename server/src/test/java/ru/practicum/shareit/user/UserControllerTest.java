package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.common.exception.DuplicateException;
import ru.practicum.shareit.common.exception.ErrorHandler;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private UserController userController;

    @MockBean
    private UserService userService;

    private MockMvc mvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static UserCreateDto userCreateDto;
    private static UserDto userDto;
    private static UserUpdateDto userUpdateDto;

    @BeforeEach
    public void setUp() {
        userCreateDto = UserCreateDto.builder().name("Люся").email("Lusya@mail.ru").build();
        userDto = UserDto.builder().id(7L).name("Люся").email("Lusya@mail.ru").build();
        userUpdateDto = UserUpdateDto.builder().name("Люся").email("Lusya@mail.ru").build();
        mvc = MockMvcBuilders.standaloneSetup(userController).setControllerAdvice(new ErrorHandler()).build();
    }

    @Test
    void createCorrectUserAndThenStatusIsOk() throws Exception {
        when(userService.create(userCreateDto)).thenReturn(userDto);

        mvc.perform(post("/users").contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto))).andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value("7"))
                .andExpect(MockMvcResultMatchers.jsonPath("name").value("Люся"))
                .andExpect(MockMvcResultMatchers.jsonPath("email").value("Lusya@mail.ru"));
    }

    @Test
    void updateCorrectUserAndThenStatusIsOk() throws Exception {
        UserDto updated = UserDto.builder().id(7L).name("Марсик").email("marsik@mail.ru").build();
        when(userService.update(1L, userUpdateDto)).thenReturn(updated);

        mvc.perform(patch("/users/1").contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto))).andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value("7"))
                .andExpect(MockMvcResultMatchers.jsonPath("name").value("Марсик"))
                .andExpect(MockMvcResultMatchers.jsonPath("email").value("marsik@mail.ru"));
    }

    @Test
    void updateUserWithDuplicateEmailAndThenStatusIsConflict() throws Exception {
        when(userService.update(5L, userUpdateDto))
                .thenThrow(new DuplicateException("User with email=marsik@mail.ru already exists"));

        assertThatThrownBy(() -> mvc.perform(patch("/users/5").contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(result -> assertInstanceOf(DuplicateException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("User with email=marsik@mail.ru already exists",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()))
                .andExpect(status().isConflict()));
    }

    @Test
    void updateNotExistsUserAndThenStatusIsNotFound() throws Exception {
        Mockito.when(userService.update(8L, userUpdateDto))
                .thenThrow(new NotFoundException("User with id=8 not found"));

        mvc.perform(patch("/users/8").contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("User with id=8 not found",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCorrectUserByIdAndThenStatusIsOk() throws Exception {
        when(userService.getById(7L)).thenReturn(userDto);

        mvc.perform(MockMvcRequestBuilders.get("/users/7")).andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value("7"))
                .andExpect(MockMvcResultMatchers.jsonPath("name").value("Люся"))
                .andExpect(MockMvcResultMatchers.jsonPath("email").value("Lusya@mail.ru"));
    }

    @Test
    void getNotExistsUserByIdAndThenStatusIsNotFound() throws Exception {
        when(userService.getById(7L)).thenThrow(new NotFoundException("User with id=7 not found"));

        mvc.perform(MockMvcRequestBuilders.get("/users/7"))
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("User with id=7 not found",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCorrectUserByIdAndThenStatusIsOk() throws Exception {
        mvc.perform(delete("/users/7").contentType("application/json")).andExpect(status().isOk());
    }

}
