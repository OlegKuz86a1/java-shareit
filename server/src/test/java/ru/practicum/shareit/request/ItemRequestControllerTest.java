package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.common.exception.ErrorHandler;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestCreate;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.service.ItemRequestService;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private ItemRequestController requestController;

    @MockBean
    private ItemRequestService requestService;

    private MockMvc mvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static ItemRequestCreate requestCreate;
    private static ItemRequestDto requestDto;


    @BeforeEach
    public void setUp() {
        requestCreate = ItemRequestCreate.builder().description("Нужен кот").build();
        requestDto = ItemRequestDto.builder().id(1L).description("Нужен кот").requestor(7L)
                .dateRequestCreated(LocalDateTime.now()).build();
        mvc = MockMvcBuilders.standaloneSetup(requestController).setControllerAdvice(new ErrorHandler()).build();
    }

    @Test
    void createCorrectRequestThenStatusIsOk() throws Exception {
        when(requestService.create(1L, requestCreate)).thenReturn(requestDto);
        mvc.perform(post("/requests").contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(requestCreate)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("requestor").value("7"))
                .andExpect(MockMvcResultMatchers.jsonPath("description").value("Нужен кот"))
                .andExpect(MockMvcResultMatchers.jsonPath("created").exists());
    }

    @Test
    void createItemRequestByNotExistsUserThenStatusNotFound() throws Exception {
        when(requestService.create(7L, requestCreate)).thenThrow(new NotFoundException("User with id=7 not found"));
        mvc.perform(post("/requests").contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 7L)
                        .content(objectMapper.writeValueAsString(requestCreate)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("User with id=7 not found",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void getRequestsByUserIdAndThenStatusOk() throws Exception {
        ItemRequestDtoWithItems firstRequest = ItemRequestDtoWithItems.builder().id(1L)
                .description("Нужен лом")
                .items(List.of(ItemDto.builder()
                        .id(1L).name("лом").description("стальной лом")
                        .isAvailable(true).requestId(1L).ownerId(7L).build()))
                .build();
        ItemRequestDtoWithItems secondRequest = ItemRequestDtoWithItems.builder().id(2L)
                .description("Нужен фен")
                .items(List.of(ItemDto.builder()
                        .id(3L).name("фен").description("большой фен")
                        .isAvailable(true).requestId(2L).ownerId(8L).build()))
                .build();

        List<ItemRequestDtoWithItems> listRequest = List.of(firstRequest, secondRequest);

        when(requestService.getRequestsByUserId(5L)).thenReturn(listRequest);

        mvc.perform(get("/requests").contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 5L).content(objectMapper.writeValueAsString(requestCreate)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value("Нужен лом"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].items.[0].id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].items.[0].ownerId").value("7"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].items.[0].name").value("лом"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].items.[0].description").value("стальной лом"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].items.[0].available").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value("2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].description").value("Нужен фен"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].items.[0].id").value("3"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].items.[0].ownerId").value("8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].items.[0].name").value("фен"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].items.[0].description").value("большой фен"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].items.[0].available").value(true));
    }

    @Test
    void getRequestsByNotExistsUserAndThenStatusNotFound() throws Exception {
        when(requestService.getRequestsByUserId(7L)).thenThrow(new NotFoundException("User with id=7 not found"));
        mvc.perform(get("/requests").contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 7L)
                        .content(objectMapper.writeValueAsString(requestCreate)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("User with id=7 not found",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void getAllRequestsThenStatusIsOk() throws Exception {
        ItemRequestDtoWithItems firstRequest = ItemRequestDtoWithItems.builder().id(1L)
                .description("Нужен лом")
                .items(List.of(ItemDto.builder()
                        .id(1L).name("лом").description("стальной лом")
                        .isAvailable(true).requestId(1L).ownerId(7L).build()))
                .build();
        ItemRequestDtoWithItems secondRequest = ItemRequestDtoWithItems.builder().id(2L)
                .description("Нужен фен")
                .items(List.of(ItemDto.builder()
                        .id(3L).name("фен").description("большой фен")
                        .isAvailable(true).requestId(2L).ownerId(8L).build()))
                .build();
        List<ItemRequestDtoWithItems> listRequest = List.of(firstRequest, secondRequest);

        when(requestService.getAllRequests(5L, 1, 10)).thenReturn(listRequest);

        mvc.perform(get("/requests/all?from=1&size=10").contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 5L).content(objectMapper.writeValueAsString(requestCreate)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value("Нужен лом"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].items.[0].id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].items.[0].ownerId").value("7"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].items.[0].name").value("лом"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].items.[0].description").value("стальной лом"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].items.[0].available").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value("2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].description").value("Нужен фен"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].items.[0].id").value("3"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].items.[0].ownerId").value("8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].items.[0].name").value("фен"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].items.[0].description").value("большой фен"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].items.[0].available").value(true));
    }

    @Test
    void getRequestByIdThenStatusIsOk() throws Exception {
        ItemRequestDtoWithItems firstRequest = ItemRequestDtoWithItems.builder().id(1L)
                .description("Нужен лом")
                .items(List.of(ItemDto.builder()
                        .id(1L).name("лом").description("стальной лом")
                        .isAvailable(true).requestId(1L).ownerId(7L).build()))
                .build();

        when(requestService.getRequestById(5L, 1L)).thenReturn(firstRequest);

        mvc.perform(get("/requests/1").contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 5L).content(objectMapper.writeValueAsString(requestCreate)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("description").value("Нужен лом"))
                .andExpect(MockMvcResultMatchers.jsonPath("items.[0].id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("items.[0].ownerId").value("7"))
                .andExpect(MockMvcResultMatchers.jsonPath("items.[0].name").value("лом"))
                .andExpect(MockMvcResultMatchers.jsonPath("items.[0].description").value("стальной лом"))
                .andExpect(MockMvcResultMatchers.jsonPath("items.[0].available").value(true));
    }

    @Test
    void getRequestByIdNotExistsUserAndThenStatusNotFound() throws Exception {
        when(requestService.getRequestById(7L, 3L))
                .thenThrow(new NotFoundException("User with id=7 not found"));

        mvc.perform(get("/requests/3").contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 7L)
                        .content(objectMapper.writeValueAsString(requestCreate)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("User with id=7 not found",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void getRequestByIdNotExistsRequestAndThenStatusNotFound() throws Exception {
        when(requestService.getRequestById(7L, 3L))
                .thenThrow(new NotFoundException("Item request with id=3 not found"));

        mvc.perform(get("/requests/3").contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 7L)
                        .content(objectMapper.writeValueAsString(requestCreate)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("Item request with id=3 not found",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }


}
