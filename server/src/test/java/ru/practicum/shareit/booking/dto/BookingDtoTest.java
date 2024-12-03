package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingDtoTest {

    @Autowired
    JacksonTester<BookingDto> jacksonTester;

    @Test
    void bookingDtoTest() throws IOException {
        LocalDateTime timeOfCreate = LocalDateTime.now();
        LocalDateTime start = LocalDateTime.now().plusMinutes(30L);
        LocalDateTime end = LocalDateTime.now().plusDays(2L);
        UserDto bookerDto = UserDto.builder().id(13L).name("Марсик").email("marsik@mail.ru").build();
        User owner = User.builder().id(7L).name("Люся").email("lusya@mail.ru").build();

        ItemRequest request = ItemRequest.builder().id(8L).description("Нннада!").dateRequestCreated(timeOfCreate).build();

        ItemDto itemDto = ItemDto.builder().id(9L).name("мышка").description("HP").isAvailable(true).requestId(request.getId())
                .ownerId(owner.getId()).build();
        BookingDto bookingDto = BookingDto.builder().item(itemDto).booker(bookerDto)
                .start(start).end(end).status(BookingStatus.WAITING).build();

        JsonContent<BookingDto> dtoJsonContent = jacksonTester.write(bookingDto);
        assertThat(dtoJsonContent).extractingJsonPathNumberValue("$.item.id").isEqualTo(9);
        assertThat(dtoJsonContent).extractingJsonPathStringValue("$.item.name").isEqualTo("мышка");
        assertThat(dtoJsonContent).extractingJsonPathStringValue("$.item.description").isEqualTo("HP");
        assertThat(dtoJsonContent).extractingJsonPathNumberValue("$.item.requestId").isEqualTo(8);
        assertThat(dtoJsonContent).extractingJsonPathNumberValue("$.item.ownerId").isEqualTo(7);
        assertThat(dtoJsonContent).extractingJsonPathNumberValue("$.booker.id").isEqualTo(13);
        assertThat(dtoJsonContent).extractingJsonPathStringValue("$.booker.name").isEqualTo("Марсик");
        assertThat(dtoJsonContent).extractingJsonPathStringValue("$.booker.email").isEqualTo("marsik@mail.ru");
        assertThat(dtoJsonContent).extractingJsonPathStringValue("$.start")
                .isEqualTo(start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(dtoJsonContent).extractingJsonPathStringValue("$.end")
                .isEqualTo(end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(dtoJsonContent).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }
}
