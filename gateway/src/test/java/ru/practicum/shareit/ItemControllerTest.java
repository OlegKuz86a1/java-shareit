package ru.practicum.shareit;

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
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.ItemController;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
public class ItemControllerTest {

    @Autowired
    private ItemController itemController;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;

    private static ResponseEntity<Object> response;
    private static CreateCommentDto createCommentDto;
    private static ItemCreateDto itemCreateDto;
    private static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";

    @BeforeAll
    public static void beforeAll() {
        response = ResponseEntity.status(HttpStatus.OK).build();
        itemCreateDto = ItemCreateDto.builder().name("фотоаппарат").description("профессиональный, Nikon")
                .isAvailable(true).build();
    }

    @BeforeEach
    void settingUpEnvironment(){
        mockMvc = MockMvcBuilders.standaloneSetup(itemController).setControllerAdvice(new ErrorHandler()).build();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void correctItemCreatedAndThenStatusIsOk() throws Exception {
        Mockito.when(itemClient.create(3L, itemCreateDto)).thenReturn(response);

        mockMvc.perform(post("/items").characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .header(SHARER_USER_ID_HEADER, 3L)
                .content(objectMapper.writeValueAsString(itemCreateDto))).andExpect(status().isOk());

        Mockito.verify(itemClient,Mockito.times(1)).create(3L, itemCreateDto);

    }

    @Test
    void itemCreatedWithNameIsNullAndThenStatusIsBadRequest() throws Exception {
        ItemCreateDto createDto = ItemCreateDto.builder()
                .description("профессиональный, Nikon")
                .isAvailable(true)
                .build();

        mockMvc.perform(post("/items").characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID_HEADER, 5L)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andExpect(status().isBadRequest());

        Mockito.verify(itemClient, Mockito.never()).create(5L, createDto);
    }

    @Test
    void itemCreatedWithDescriptionIsNullAndThenStatusIsBadRequest() throws Exception {
        ItemCreateDto createDto = ItemCreateDto.builder()
                .name("фотоаппарат")
                .isAvailable(true)
                .build();

        mockMvc.perform(post("/items")
                        .characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).header(SHARER_USER_ID_HEADER, 5L)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andExpect(status().isBadRequest());

        Mockito.verify(itemClient, Mockito.never()).create(5L, createDto);
    }

    @Test
    void itemCreatedWithAvailableIsNullAndThenStatusIsBadRequest() throws Exception {
        ItemCreateDto createDto = ItemCreateDto.builder()
                .name("фотоаппарат")
                .description("профессиональный, Nikon")
                .build();

        mockMvc.perform(post("/items")
                        .characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).header(SHARER_USER_ID_HEADER, 5L)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andExpect(status().isBadRequest());

        Mockito.verify(itemClient, Mockito.never()).create(5L, createDto);
    }
    @Test
    void correctItemUpdateAndThenStatusIsOk() throws Exception {
        Mockito.when(itemClient.update(2L, 3L, itemCreateDto)).thenReturn(response);

        mockMvc.perform(patch("/items/3").characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .header(SHARER_USER_ID_HEADER, 2L)
                .content(objectMapper.writeValueAsString(itemCreateDto))).andExpect(status().isOk());

        Mockito.verify(itemClient, Mockito.times(1)).update(2L,3L, itemCreateDto);
    }

    @Test
    void itemUpdateWithNegativeItemIdAndThenStatusIsBadRequest() throws Exception {
        mockMvc.perform(patch("/items/-6").characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID_HEADER, 1L)
                        .content(objectMapper.writeValueAsString(itemCreateDto)))
                .andExpect(result -> assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()))
                .andExpect(status().isBadRequest());

        Mockito.verify(itemClient, Mockito.never()).update(1L,-6L, itemCreateDto);
    }

    @Test
    void getCorrectItemByIdAndThenStatusIsOk() throws Exception {
        Mockito.when(itemClient.getItem(3L, 1L)).thenReturn(response);

        mockMvc.perform(get("/items/1").header(SHARER_USER_ID_HEADER, 3L))
                .andExpect(status().isOk());

        Mockito.verify(itemClient, Mockito.times(1)).getItem(3L, 1L);
    }

    @Test
    void getItemByNegativeIdAndThenStatusIsBadRequest() throws Exception {
        mockMvc.perform(get("/items/-5").header(SHARER_USER_ID_HEADER, 6L))
                .andExpect(result -> assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()))
                .andExpect(status().isBadRequest());

        Mockito.verify(itemClient, Mockito.never()).getItem(6L, -5L);
    }

    @Test
    void getCorrectItemByUserIdAndThenStatusIsOk() throws Exception {
        Mockito.when(itemClient.allItems(7L, 1, 10)).thenReturn(response);

        mockMvc.perform(get("/items?from=1&size=10").header(SHARER_USER_ID_HEADER, 7L))
                .andExpect(status().isOk());

        Mockito.verify(itemClient, Mockito.times(1)).allItems(7L, 1, 10);
    }

    @Test
    void getItemByNegativeUserIdAndThenStatusIsBadRequest() throws Exception {
        mockMvc.perform(get("/items?from=1&size=10")
                        .header(SHARER_USER_ID_HEADER, -7L))
                .andExpect(result -> assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()))
                .andExpect(status().isBadRequest());

        Mockito.verify(itemClient, Mockito.never())
                .allItems(-7L, 1, 10);
    }

    @Test
    void getItemsForRentAndThenStatusIsOk() throws Exception {
        Mockito.when(itemClient.getItemsForRent(7, "text")).thenReturn(response);

        mockMvc.perform(get("/items/search?text=text").header(SHARER_USER_ID_HEADER, 7L))
                .andExpect(status().isOk());

        Mockito.verify(itemClient, Mockito.times(1)).getItemsForRent(7, "text");
    }

    @Test
    void getItemsForRentWithBlankTextAndThenStatusIsOkAndReturnEmptyList() throws Exception {
        mockMvc.perform(get("/items/search?text=").header(SHARER_USER_ID_HEADER, 7L))
                .andExpect(jsonPath("$").doesNotExist()).andExpect(status().isOk());

        Mockito.verify(itemClient, Mockito.times(1))
                .getItemsForRent(7,"");
    }

    @Test
    void postCorrectCommentAndThenStatusIsOk() throws Exception {
        CreateCommentDto commentDto = CreateCommentDto.builder().text("отличный фотоаппарат").build();

        Mockito.when(itemClient.addComment(1L, commentDto, 2L))
                .thenReturn(response);

        mockMvc.perform(post("/items/2/comment").characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID_HEADER, 1L)
                        .content(objectMapper.writeValueAsString(commentDto))).andExpect(status().isOk());

        Mockito.verify(itemClient, Mockito.times(1))
                .addComment(1L, commentDto, 2L);
    }

    @Test
    void postCommentWithoutTextAndThenStatusIsBadRequest() throws Exception {
        CreateCommentDto commentDto = CreateCommentDto.builder().text("").build();

        mockMvc.perform(post("/items/3/comment")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID_HEADER, 1L)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andExpect(status().isBadRequest());

        Mockito.verify(itemClient, Mockito.never()).addComment(1L, commentDto, 3L);
    }





}
