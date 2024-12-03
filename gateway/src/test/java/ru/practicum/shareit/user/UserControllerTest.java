package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private UserController userController;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserClient userClient;

    private static UserCreateDto createDto;
    private static ResponseEntity<Object> response;
    private static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";

    @BeforeAll
    public static void beforeAll() {
        createDto = UserCreateDto.builder()
                .name("name")
                .email("mail@mail.ru")
                .build();
    }

    @BeforeEach
    void settingUpEnvironment() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(new ErrorHandler())
                .build();
        objectMapper.registerModule(new JavaTimeModule());
    }


    @Test
    void whenCorrectUserCreatedAndThenStatusIsOk() throws Exception {
        Mockito.when(userClient.create(createDto)).thenReturn(response);

        mockMvc.perform(post("/users").characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto))).andExpect(status().isOk());

        Mockito.verify(userClient,Mockito.times(1)).create(createDto);

    }

    @Test
    void whenUserCreatedWithoutEmailAndThenStatusBadRequest() throws Exception {
        UserCreateDto userWithoutEmail = UserCreateDto.builder().name("name").email(" ").build();

        mockMvc.perform(post("/users").characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userWithoutEmail)))
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andExpect(status().isBadRequest());

        Mockito.verify(userClient, Mockito.never()).create(Mockito.any(UserCreateDto.class));
    }

    @Test
    void whenUserCreatedWithWrongEmailAndThenStatusBadRequest() throws Exception {
        UserCreateDto userWrongEmail = UserCreateDto.builder().name("name").email("email$$mail.ru").build();

        mockMvc.perform(post("/users").characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userWrongEmail)))
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andExpect(status().isBadRequest());

        Mockito.verify(userClient, Mockito.never()).create(Mockito.any(UserCreateDto.class));
    }

    @Test
    void whenCorrectUserUpdateAndThenStatusIsOk() throws Exception {
        UserUpdateDto updateDto = UserUpdateDto.builder().email("new@email.com").name("New Name").build();

        Mockito.when(userClient.update(1L, updateDto)).thenReturn(response);

        mockMvc.perform(patch("/users/1").characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID_HEADER, 1L)
                        .content(objectMapper.writeValueAsString(updateDto))).andExpect(status().isOk());

        Mockito.verify(userClient,Mockito.times(1)).update(1L, updateDto);
    }

    @Test
    void whenUserUpdatedWithNegativeIdAndThenStatusBadRequest() throws Exception {

        mockMvc.perform(patch("/users/-8").characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest());

        Mockito.verify(userClient, Mockito.never()).update(Mockito.anyLong(), Mockito.any(UserUpdateDto.class));
    }

    @Test
    void whenUserValidByIdAndThenStatusIsOk() throws Exception {
        Mockito.when(userClient.getById(8L)).thenReturn(response);

        mockMvc.perform(get("/users/8")).andExpect(status().isOk());

        Mockito.verify(userClient, Mockito.times(1)).getById(8L);
    }

    @Test
    void whenUserByNegativeIdAndThenStatusIsBadRequest() throws Exception {
        mockMvc.perform(get("/users/-8").header(SHARER_USER_ID_HEADER, 1L))
                .andExpect(result -> assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()))
                .andExpect(status().isBadRequest());

        Mockito.verify(userClient, Mockito.never()).getById(Mockito.anyLong());
    }

    @Test
    void DeleteByNegativeIdThenStatusIsBadRequest() throws Exception {
        mockMvc.perform(delete("/users/-8"))
                .andExpect(result -> assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()))
                .andExpect(status().isBadRequest());

        Mockito.verify(userClient, Mockito.never()).delete(Mockito.anyLong());
    }

    @Test
    void DeleteByCorrectIdAndThenStatusIsOk() throws Exception {
        Mockito.when(userClient.delete(8L)).thenReturn(response);

        mockMvc.perform(delete("/users/8")).andExpect(status().isOk());

        Mockito.verify(userClient, Mockito.times(1)).delete(8L);
    }

}
