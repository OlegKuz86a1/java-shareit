package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.common.exception.ErrorHandler;
import ru.practicum.shareit.common.exception.IllegalDataException;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.repository.UserRepository;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private ItemController itemController;

    @MockBean
    private ItemService itemService;
    @MockBean
    private UserRepository userRepository;

    private MockMvc mvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private ItemDto itemDto;
    private ItemCreateDto itemCreateDto;
    private RequestItem updated;
    private CommentDto commentDto;
    private CreateCommentDto createCommentDto;

    @BeforeEach
    void setUp() {;
        itemDto = ItemDto.builder().id(1L).name("фен").description("новый").ownerId(7L).isAvailable(true).build();
        itemCreateDto = ItemCreateDto.builder().name("фен").description("новый").ownerId(7L).isAvailable(true).build();
        updated = RequestItem.builder().id(1L).name("фен").description("БУ").isAvailable(true).build();
        commentDto = CommentDto.builder().id(5L).created(LocalDateTime.now()).text("хороший").item(itemDto).build();
        createCommentDto = CreateCommentDto.builder().itemId(1L).text("хороший").build();
        mvc = MockMvcBuilders.standaloneSetup(itemController).setControllerAdvice(new ErrorHandler()).build();
        objectMapper.registerModule(new JavaTimeModule());

    }

    @Test
    void getAllItemsThenStatusOk() throws Exception {
        ItemDtoAllFields oneItem = ItemDtoAllFields.builder().id(21L).name("мышка").description("HP").isAvailable(true).build();
        ItemDtoAllFields twoItem = ItemDtoAllFields.builder().id(12L).name("ноут").description("HP").isAvailable(true).build();
        List<ItemDtoAllFields> dtoAllFieldsList = List.of(oneItem, twoItem);
        when(itemService.allItems(7L, 1, 10)).thenReturn(dtoAllFieldsList);

        mvc.perform(get("/items?from=1&size=10")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 7L)).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("21"))
                .andExpect(jsonPath("$[0].name").value("мышка"))
                .andExpect(jsonPath("$[0].description").value("HP"))
                .andExpect(jsonPath("$[1].id").value("12"))
                .andExpect(jsonPath("$[1].name").value("ноут"))
                .andExpect(jsonPath("$[1].description").value("HP"));
    }

    @Test
    void getItemByIdAndThenStatusOk() throws Exception {
        ItemDtoAllFields oneItem = ItemDtoAllFields.builder().id(21L).name("мышка").description("HP").isAvailable(true)
                .ownerId(7L).build();
        when(itemService.getItem(anyLong(), anyLong())).thenReturn(oneItem);
        mvc.perform(get("/items/21").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).header("X-Sharer-User-Id", 7L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value("21"))
                .andExpect(jsonPath("name").value("мышка"))
                .andExpect(jsonPath("description").value("HP"))
                .andExpect(jsonPath("ownerId").value("7"))
                .andExpect(jsonPath("available").value(true));

    }
    @Test
    void getItemByNotExistsItemAndThenStatusNotFound() throws Exception {
        when(itemService.getItem(7L, 1L)).thenThrow(new NotFoundException("Item not found with id: 1"));

        mvc.perform(get("/items/1").contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 7L))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("Item not found with id: 1",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }



    @Test
    void getItemsForRentAndThenStatusOk() throws Exception {
        ResponseItem responseItem = ResponseItem.builder().name("мышка").description("HP").isAvailable(true).build();
        when(itemService.getItemsForRent(7L, "HP")).thenReturn(List.of(responseItem));
        mvc.perform(get("/items/search?text=HP")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 7L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].name").value("мышка"))
                .andExpect(jsonPath("[0].description").value("HP"))
                .andExpect(jsonPath("[0].available").value(true));
    }

    @Test
    void createItemAndThenStatusOk() throws Exception {
        when(itemService.addItem(itemCreateDto)).thenReturn(itemDto);

        mvc.perform(post("/items").contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 7L)
                .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value("1"))
                .andExpect(jsonPath("name").value("фен"))
                .andExpect(jsonPath("description").value("новый"))
                .andExpect(jsonPath("ownerId").value("7"))
                .andExpect(jsonPath("available").value(true));

    }

    @Test
    void createItemByNotExistsUserAndThenStatusIsNotFound() throws Exception {
        when(itemService.addItem(itemCreateDto))
                .thenThrow(new NotFoundException("User not found with id: 7"));

        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 7L)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("User not found with id: 7",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));

    }

    @Test
    void addValidCommentAndThenStatusIsOk() throws Exception {
        when(itemService.addComment(any(CreateCommentDto.class))).thenReturn(commentDto);

        mvc.perform(post("/items/5/comment").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).header("X-Sharer-User-Id", 2L)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value("5"))
                .andExpect(jsonPath("text").value("хороший"))
                .andExpect(jsonPath("created").exists());
    }

    @Test
    void addCommentIfBookingCurrentThenStatusIsBadRequest() throws Exception {
        when(itemService.addComment(any(CreateCommentDto.class))).thenThrow(new IllegalDataException("Item in approved booking"));

        mvc.perform(post("/items/1/comment")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2L)
                        .content(objectMapper.writeValueAsString(createCommentDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(IllegalDataException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("Item in approved booking",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void updateItemWithoutExtendUserAndThenStatusIsNotFound() throws Exception {
        when(itemService.updateItem(7L, updated)).thenThrow(new NotFoundException("User not found with id: 7"));

        mvc.perform(patch("/items/1").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).header("X-Sharer-User-Id", 7L)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("User not found with id: 7",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));

    }

    @Test
    void updateItemAndThenStatusIsOk() throws Exception {
        when(itemService.updateItem(7L, updated)).thenReturn(itemDto);

        mvc.perform(patch("/items/1").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).header("X-Sharer-User-Id", 7L)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value("1"))
                .andExpect(jsonPath("name").value("фен"))
                .andExpect(jsonPath("description").value("новый"));
    }

    @Test
    void updateItemWithoutExtendItemAndThenStatusIsNotFound() throws Exception {
        when(itemService.updateItem(7L, updated)).thenThrow(new NotFoundException("Item not found with id: 1"));

        mvc.perform(patch("/items/1").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).header("X-Sharer-User-Id", 7L)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("Item not found with id: 1",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));

    }

}
