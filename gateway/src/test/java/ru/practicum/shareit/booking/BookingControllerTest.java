package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.exception.IllegalDataException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
public class BookingControllerTest {
    @Autowired
    private BookingController bookingController;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingClient bookingClient;

    private static BookingCreateDto bookingCreateDto;
    private static ResponseEntity<Object> response;
    private static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";

    @BeforeAll
    public static void setBookingCreateDto() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        bookingCreateDto = BookingCreateDto.builder().itemId(1L).bookerId(2L).start(start).end(end).build();
        response = ResponseEntity.status(HttpStatus.OK).build();
    }

    @BeforeEach
    void settingUpEnvironment() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).setControllerAdvice(new ErrorHandler()).build();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void createCorrectBookingAndThenStatusIsOk() throws Exception {
        Mockito.when(bookingClient.create(7L, bookingCreateDto)).thenReturn(response);

        mockMvc.perform(post("/bookings").characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .header(SHARER_USER_ID_HEADER, 7L)
                .content(objectMapper.writeValueAsString(bookingCreateDto))).andExpect(status().isOk());

        Mockito.verify(bookingClient, Mockito.times(1))
                .create(7L, bookingCreateDto);
    }

    @Test
    void createBookingWithNegativeItemIdAndThenStatusIsBadRequest() throws Exception {
        BookingCreateDto negativeItemId = BookingCreateDto.builder()
                .bookerId(6L)
                .itemId(-8L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(1))
                .build();

        mockMvc.perform(post("/bookings")
                .characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).header(SHARER_USER_ID_HEADER, 6L)
                .content(objectMapper.writeValueAsString(negativeItemId))).andExpect(status().isBadRequest());

        Mockito.verify(bookingClient, Mockito.never())
                .create(Mockito.anyLong(), Mockito.any(BookingCreateDto.class));
    }

    @Test
    void createBookingWithStartBeforeNowAndThenStatusIsBadRequest() throws Exception {
        BookingCreateDto startBeforeNow = BookingCreateDto.builder()
                .bookerId(2L)
                .itemId(3L)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .build();

        mockMvc.perform(post("/bookings").characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .header(SHARER_USER_ID_HEADER, 2L)
                        .content(objectMapper.writeValueAsString(startBeforeNow)))
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andExpect(status().isBadRequest());

        Mockito.verify(bookingClient, Mockito.never()).create(Mockito.anyLong(), Mockito.any(BookingCreateDto.class));
    }

    @Test
    void createBookingWithStartBeforeEndAndThenStatusIsBadRequest() throws Exception {
        Mockito.when(bookingClient.create(5L, bookingCreateDto))
                .thenThrow(new IllegalDataException("the start cannot be before the end"));

        mockMvc.perform(post("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).header(SHARER_USER_ID_HEADER, 5L)
                        .content(objectMapper.writeValueAsString(bookingCreateDto)))
                .andExpect(result -> assertInstanceOf(IllegalDataException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("the start cannot be before the end",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()))
                .andExpect(status().isBadRequest());

        Mockito.verify(bookingClient, Mockito.times(1)).create(5L, bookingCreateDto);
    }

    @Test
    void updateCorrectBookingAndThenStatusIsOk() throws Exception {
        Mockito.when(bookingClient.update(3L, 5L, true)).thenReturn(response);

        mockMvc.perform(patch("/bookings/5?approved=true").header(SHARER_USER_ID_HEADER, 3L))
                .andExpect(status().isOk());

        Mockito.verify(bookingClient, Mockito.times(1))
                .update(3L, 5L, true);
    }

    @Test
    void getCorrectBookingByIdAndThenStatusIsOk() throws Exception {
        Mockito.when(bookingClient.getBooking(3L, 5L)).thenReturn(response);

        mockMvc.perform(get("/bookings/5").header(SHARER_USER_ID_HEADER, 3L))
                .andExpect(status().isOk());

        Mockito.verify(bookingClient, Mockito.times(1)).getBooking(3L, 5L);
    }

    @Test
    void getBookingByNegativeIdAndThenStatusIsBadRequest() throws Exception {
        mockMvc.perform(get("/bookings/-7").header(SHARER_USER_ID_HEADER, 1L))
                .andExpect(status().isBadRequest());

        Mockito.verify(bookingClient, Mockito.never()).getBooking(1L, -7L);
    }

    @Test
    void getAllByUserAndThenStatusIsOk() throws Exception {
        Mockito.when(bookingClient.getAllByUser(3L, BookingStatus.ALL, 1, 10)).thenReturn(response);

        mockMvc.perform(get("/bookings?state=ALL").header(SHARER_USER_ID_HEADER, 3L))
                .andExpect(status().isOk());

        Mockito.verify(bookingClient, Mockito.times(1))
                .getAllByUser(3L, BookingStatus.ALL, 1, 10);
    }


    @Test
    void getAllByUserAndInvalidFromThenIsBadRequest() throws Exception {
        mockMvc.perform(get("/bookings?state=ALL&from=-8").header(SHARER_USER_ID_HEADER, 3L))
                .andExpect(result -> assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("getAllByUser.from: must be greater than 0",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenGetBookingsByOwnerIdThenStatusIsOk() throws Exception {
        Mockito.when(bookingClient.getAllByOwner(7L, BookingStatus.APPROVED, 1, 10))
                .thenReturn(response);

        mockMvc.perform(get("/bookings/owner?state=APPROVED").header(SHARER_USER_ID_HEADER, 7L))
                .andExpect(status().isOk());

        Mockito.verify(bookingClient, Mockito.times(1))
                .getAllByOwner(7L, BookingStatus.APPROVED, 1, 10);
    }

    @Test
    void getAllByOwnerAndInvalidFromThenIsBadRequest() throws Exception {
        mockMvc.perform(get("/bookings/owner?state=ALL&from=-8").header(SHARER_USER_ID_HEADER, 3L))
                .andExpect(result -> assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("getAllByOwner.from: must be greater than 0",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()))
                .andExpect(status().isBadRequest());
    }

}
