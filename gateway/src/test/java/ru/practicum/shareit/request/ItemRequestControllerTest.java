package ru.practicum.shareit.request;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.request.dto.ItemRequestCreate;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
public class ItemRequestControllerTest {

    @Autowired
    private ItemRequestController itemRequestController;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RequestClient requestClient;

    private static ItemRequestCreate itemRequestCreate;
    private static ResponseEntity<Object> response;
    private static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";

    @BeforeAll
    public static void beforeAll() {
        itemRequestCreate = ItemRequestCreate.builder().description("мне нужен фотоаппарат").build();
        response = ResponseEntity.status(HttpStatus.OK).build();
    }

    @BeforeEach
    void settingUpEnvironment() {
        mockMvc = MockMvcBuilders.standaloneSetup(itemRequestController).setControllerAdvice(new ErrorHandler()).build();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void whenCorrectItemRequestCreatedAndThenStatusIsOk() throws Exception {
        Mockito.when(requestClient.create(1L, itemRequestCreate)).thenReturn(response);

        mockMvc.perform(post("/requests")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(SHARER_USER_ID_HEADER, 1L)
                .content(objectMapper.writeValueAsString(itemRequestCreate)))
                .andExpect(status().isOk());

        Mockito.verify(requestClient, Mockito.times(1)).create(1L, itemRequestCreate);
    }

    @Test
    void whenItemRequestCreatedWithoutDescriptionAndThenStatusBadRequest() throws Exception {
        ItemRequestCreate request = new ItemRequestCreate();

        mockMvc.perform(post("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID_HEADER, 1L)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andExpect(status().isBadRequest());

        Mockito.verify(requestClient, Mockito.never()).create(Mockito.anyLong(), Mockito.any(ItemRequestCreate.class));
    }

    @Test
    void whenItemRequestCreatedWithBlankDescriptionAndThenStatusBadRequest() throws Exception {
        ItemRequestCreate request = ItemRequestCreate.builder().description("").build();

        mockMvc.perform(post("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID_HEADER, 1L)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andExpect(status().isBadRequest());

        Mockito.verify(requestClient, Mockito.never()).create(Mockito.anyLong(), Mockito.any(ItemRequestCreate.class));
    }

    @Test
    void whenGetAllItemRequestByUserIdAndThenStatusIsOk() throws Exception {
        Mockito.when(requestClient.getRequestsByUserId(8L)).thenReturn(response);

        mockMvc.perform(get("/requests").header(SHARER_USER_ID_HEADER, 8L))
                .andExpect(status().isOk());

        Mockito.verify(requestClient, Mockito.times(1)).getRequestsByUserId(8L);
    }

    @Test
    void whenItemRequestValidByIdAndThenStatusIsOk() throws Exception {
        Mockito.when(requestClient.getRequestById(8L, 7L)).thenReturn(response);

        mockMvc.perform(get("/requests/7").header(SHARER_USER_ID_HEADER, 8L))
                .andExpect(status().isOk());

        Mockito.verify(requestClient, Mockito.times(1)).getRequestById(8L, 7L);
    }

    @Test
    void whenGetItemRequestByNegativeIdAndThenStatusBadRequest() throws Exception {
        mockMvc.perform(get("/requests/-7").header(SHARER_USER_ID_HEADER, 8L))
                .andExpect(result -> assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()))
                .andExpect(status().isBadRequest());

        Mockito.verify(requestClient, Mockito.never()).getRequestById(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void whenGetAllItemRequestWithNegativeFromAndThenStatusIsBadRequest() throws Exception {

        mockMvc.perform(get("/requests/all?from=-3&size=4").header(SHARER_USER_ID_HEADER, 8L))
                .andExpect(result -> assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()))
                .andExpect(status().isBadRequest());

        Mockito.verify(requestClient, Mockito.never()).getRequestById(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void whenGetAllItemRequestAndThenStatusIsOk() throws Exception {
        Mockito.when(requestClient.getAllRequests(8L, 0, 10)).thenReturn(response);

        mockMvc.perform(get("/requests/all").header(SHARER_USER_ID_HEADER, 8L))
                .andExpect(status().isOk());

        Mockito.verify(requestClient, Mockito.times(1)).getAllRequests(8L, 0, 10);
    }

}
