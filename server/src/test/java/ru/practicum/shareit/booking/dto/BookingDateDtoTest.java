package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingDateDtoTest {
    @Autowired
    JacksonTester<BookingDateDto> jacksonTester;

    @Test
    void bookingDtoTest() throws IOException {
        LocalDateTime start = LocalDateTime.now().plusMinutes(30L);
        LocalDateTime end = LocalDateTime.now().plusDays(2L);

        BookingDateDto bookingDateDto = BookingDateDto.builder().id(13L).bookerId(69L).start(start).end(end).build();

        JsonContent<BookingDateDto> dtoJsonContent = jacksonTester.write(bookingDateDto);
        assertThat(dtoJsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(13);
        assertThat(dtoJsonContent).extractingJsonPathNumberValue("$.bookerId").isEqualTo(69);
        assertThat(dtoJsonContent).extractingJsonPathStringValue("$.start")
                .isEqualTo(start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(dtoJsonContent).extractingJsonPathStringValue("$.end")
                .isEqualTo(end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
}
