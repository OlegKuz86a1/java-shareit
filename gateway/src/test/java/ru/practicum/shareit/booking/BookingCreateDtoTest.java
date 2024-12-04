package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingCreateDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingCreateDtoTest {

    @Autowired
    JacksonTester<BookingCreateDto> jacksonTester;

    @Test
    void correctBookingDto() throws IOException {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingCreateDto bookingDto = BookingCreateDto.builder().itemId(6L).bookerId(1L).start(start).end(end).build();

        JsonContent<BookingCreateDto> jsonContent = jacksonTester.write(bookingDto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.itemId").isEqualTo(6);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.bookerId").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.start")
                .isEqualTo(start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(jsonContent).extractingJsonPathStringValue("$.end")
                .isEqualTo(end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
}