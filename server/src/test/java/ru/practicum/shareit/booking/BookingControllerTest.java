package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.common.exception.AccessDeniedException;
import ru.practicum.shareit.common.exception.BookingFailedException;
import ru.practicum.shareit.common.exception.ErrorHandler;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private BookingController bookingController;

    @MockBean
    private BookingService bookingService;
    private MockMvc mvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private BookingDto bookingDto;
    private BookingCreateDto bookingCreateDto;

    @BeforeEach
    void init() {
        bookingCreateDto = BookingCreateDto.builder().itemId(1L).bookerId(7L).build();
        bookingDto = BookingDto.builder().id(1L).booker(UserDto.builder().id(7L).name("Люся").email("lusya@mail.ru").build())
                .item(ItemDto.builder().id(17L).name("мышка").description("HP").isAvailable(true).ownerId(7L).build())
                .status(BookingStatus.WAITING).build();
        mvc = MockMvcBuilders.standaloneSetup(bookingController).setControllerAdvice(new ErrorHandler()).build();
    }

    @Test
    void createBookingByNotExistsOwnerOfItemAndThenStatusIsNotFound() throws Exception {
        when(bookingService.addBooking(bookingCreateDto)).thenThrow(new NotFoundException("User with id=7 not found"));

        mvc.perform(post("/bookings").characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 7L).content(objectMapper.writeValueAsString(bookingCreateDto)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("User with id=7 not found",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));

    }

    @Test
    void createBookingByNotExistsItemAndThenStatusIsNotFound() throws Exception {
        when(bookingService.addBooking(bookingCreateDto)).thenThrow(new NotFoundException("Item with id=17 not found"));

        mvc.perform(post("/bookings").characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 7L).content(objectMapper.writeValueAsString(bookingCreateDto)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("Item with id=17 not found",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void createCorrectBookingAndThenStatusIsOk() throws Exception {
        when(bookingService.addBooking(bookingCreateDto)).thenReturn(bookingDto);

        mvc.perform(post("/bookings").characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 7L).content(objectMapper.writeValueAsString(bookingCreateDto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("booker.id").value("7"))
                .andExpect(MockMvcResultMatchers.jsonPath("booker.name").value("Люся"))
                .andExpect(MockMvcResultMatchers.jsonPath("booker.email").value("lusya@mail.ru"))
                .andExpect(MockMvcResultMatchers.jsonPath("item.id").value("17"))
                .andExpect(MockMvcResultMatchers.jsonPath("item.name").value("мышка"))
                .andExpect(MockMvcResultMatchers.jsonPath("item.description").value("HP"))
                .andExpect(MockMvcResultMatchers.jsonPath("item.available").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("item.ownerId").value("7"))
                .andExpect(MockMvcResultMatchers.jsonPath("status").value("WAITING"));
    }

    @Test
    void createBookingByBookerWhoIsOwnerItemAndThenStatusIsBookingFailedException() throws Exception {
        when(bookingService.addBooking(bookingCreateDto))
                .thenThrow(new BookingFailedException("Booker cannot be owner of the item"));

        mvc.perform(post("/bookings").characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 7L).content(objectMapper.writeValueAsString(bookingCreateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(BookingFailedException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("Booker cannot be owner of the item",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void updateByBookingNotExistsAndThenStatusNotFoundException() throws Exception {
        when(bookingService.changeApproved(7L, 1L, true))
                .thenThrow(new NotFoundException("Booking not found with id: 1"));

        mvc.perform(patch("/bookings/1?approved=true").characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 7L).content(objectMapper.writeValueAsString(bookingCreateDto)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("Booking not found with id: 1",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void updateWhenBookingForUserNotFoundException() throws Exception {
        when(bookingService.changeApproved(7L, 1L, true))
                .thenThrow(new AccessDeniedException("booking with id=1 for the user with id=7 was not found"));

        mvc.perform(patch("/bookings/1?approved=true").characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 7L).content(objectMapper.writeValueAsString(bookingCreateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(AccessDeniedException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("booking with id=1 for the user with id=7 was not found",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void updateBookingAndThenStatusIsOk() throws Exception {
        BookingDto  bookingDtoApproved = BookingDto.builder().id(1L).booker(UserDto.builder().id(7L).name("Люся")
                .email("lusya@mail.ru").build()).item(ItemDto.builder().id(17L).name("мышка").description("HP")
                .isAvailable(true).ownerId(7L).build()).status(BookingStatus.APPROVED).build();
        when(bookingService.changeApproved(7L, 1L, true)).thenReturn(bookingDtoApproved);

        mvc.perform(patch("/bookings/1?approved=true").header("X-Sharer-User-Id", 7L))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("status").value("APPROVED"));
    }

    @Test
    void getByBookingIdByNotExistsOwnerOfItemAndThenStatusIsNotFound() throws Exception {
        when(bookingService.getById(7L, 1L)).thenThrow(new NotFoundException("User with id=7 not found"));

        mvc.perform(get("/bookings/1").characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 7L).content(objectMapper.writeValueAsString(bookingCreateDto)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("User with id=7 not found",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void getByBookingIdByNotExistsBookingAndThenStatusIsEntityNotFoundException() throws Exception {
        when(bookingService.getById(7L, 1L))
                .thenThrow(new NotFoundException("Booking with id=1 not found"));

        mvc.perform(get("/bookings/1").characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 7L).content(objectMapper.writeValueAsString(bookingCreateDto)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("Booking with id=1 not found",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void getByBookingIdAndThenStatusIsOk() throws Exception {
        when(bookingService.getById(7L, 1L)).thenReturn(bookingDto);

        mvc.perform(get("/bookings/1").characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 7L).content(objectMapper.writeValueAsString(bookingCreateDto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("booker.id").value("7"))
                .andExpect(MockMvcResultMatchers.jsonPath("booker.name").value("Люся"))
                .andExpect(MockMvcResultMatchers.jsonPath("booker.email").value("lusya@mail.ru"))
                .andExpect(MockMvcResultMatchers.jsonPath("item.id").value("17"))
                .andExpect(MockMvcResultMatchers.jsonPath("item.name").value("мышка"))
                .andExpect(MockMvcResultMatchers.jsonPath("item.description").value("HP"))
                .andExpect(MockMvcResultMatchers.jsonPath("item.available").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("item.ownerId").value("7"))
                .andExpect(MockMvcResultMatchers.jsonPath("status").value("WAITING"));
    }

    @Test
    void getAllByUserByNotExistsUserAndThenStatusIsNotFound() throws Exception {
        when(bookingService.getAllByUser(7L, BookingStatus.ALL, 1, 10))
                .thenThrow(new NotFoundException("User with id=7 not found"));

        mvc.perform(get("/bookings?state=ALL&from=1&size=10").characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 7L).content(objectMapper.writeValueAsString(bookingCreateDto)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("User with id=7 not found",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void getAllByUserAndThenStatusIsOk() throws Exception {
        BookingDto secondBooking = BookingDto.builder().id(2L).booker(UserDto.builder().id(7L).name("Люся")
                        .email("lusya@mail.ru").build()).item(ItemDto.builder().id(17L).name("мышка")
                        .description("HP").isAvailable(true).ownerId(7L).build()).status(BookingStatus.REJECTED).build();
        BookingDto thirdBooking = BookingDto.builder().id(3L).booker(UserDto.builder().id(7L).name("Люся")
                        .email("lusya@mail.ru").build()).item(ItemDto.builder().id(17L).name("мышка").description("HP")
                        .isAvailable(true).ownerId(7L).build()).status(BookingStatus.APPROVED).build();
        List<BookingDto> bookings = List.of(bookingDto, secondBooking, thirdBooking);

        when(bookingService.getAllByUser(7L, BookingStatus.ALL, 1, 10)).thenReturn(bookings);

        mvc.perform(get("/bookings?state=ALL&from=1&size=10")
                        .header("X-Sharer-User-Id", 7L))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].item.id").value("17"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].booker.id").value("7"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].status").value("WAITING"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value("2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].item.id").value("17"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].booker.id").value("7"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].status").value("REJECTED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].id").value("3"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].item.id").value("17"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].booker.id").value("7"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].status").value("APPROVED"));
    }

    @Test
    void getAllByOwnerByNotExistsUserAndThenStatusIsNotFound() throws Exception {
        when(bookingService.getAllByOwner(7L, BookingStatus.ALL, 1, 10))
                .thenThrow(new NotFoundException("User with id=7 not found"));

        mvc.perform(get("/bookings/owner?state=ALL&from=1&size=10").characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 7L).content(objectMapper.writeValueAsString(bookingCreateDto)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("User with id=7 not found",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void getAllByOwnerAndThenStatusIsOk() throws Exception {
        BookingDto secondBooking = BookingDto.builder().id(2L).booker(UserDto.builder().id(7L).name("Люся")
                .email("lusya@mail.ru").build()).item(ItemDto.builder().id(17L).name("мышка")
                .description("HP").isAvailable(true).ownerId(7L).build()).status(BookingStatus.REJECTED).build();
        BookingDto thirdBooking = BookingDto.builder().id(3L).booker(UserDto.builder().id(7L).name("Люся")
                .email("lusya@mail.ru").build()).item(ItemDto.builder().id(17L).name("мышка").description("HP")
                .isAvailable(true).ownerId(7L).build()).status(BookingStatus.APPROVED).build();
        List<BookingDto> bookings = List.of(bookingDto, secondBooking, thirdBooking);

        when(bookingService.getAllByOwner(7L, BookingStatus.ALL, 1, 10)).thenReturn(bookings);

        mvc.perform(get("/bookings/owner?state=ALL&from=1&size=10")
                        .header("X-Sharer-User-Id", 7L))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].item.id").value("17"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].booker.id").value("7"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].status").value("WAITING"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value("2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].item.id").value("17"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].booker.id").value("7"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].status").value("REJECTED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].id").value("3"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].item.id").value("17"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].booker.id").value("7"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].status").value("APPROVED"));
    }
}
