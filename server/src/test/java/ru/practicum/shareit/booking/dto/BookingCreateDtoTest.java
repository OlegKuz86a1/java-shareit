package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingCreateDtoTest {
    @Autowired
    JacksonTester<BookingCreateDto> jacksonTester;

    @Test
    void bookingCreateDtoTest() throws IOException {
        LocalDateTime start = LocalDateTime.now().plusMinutes(30L);
        LocalDateTime end = LocalDateTime.now().plusDays(2L);
        BookingCreateDto bookingCreateDto = BookingCreateDto.builder().itemId(3L).bookerId(7L)
                .start(start).end(end).status(BookingStatus.WAITING).build();

        JsonContent<BookingCreateDto> createDtoJsonContent = jacksonTester.write(bookingCreateDto);
        assertThat(createDtoJsonContent).extractingJsonPathNumberValue("$.itemId").isEqualTo(3);
        assertThat(createDtoJsonContent).extractingJsonPathNumberValue("$.bookerId").isEqualTo(7);
        assertThat(createDtoJsonContent).extractingJsonPathStringValue("$.start")
                .isEqualTo(start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(createDtoJsonContent).extractingJsonPathStringValue("$.end")
                .isEqualTo(end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(createDtoJsonContent).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }
}
