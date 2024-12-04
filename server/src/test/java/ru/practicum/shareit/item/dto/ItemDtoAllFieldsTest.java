package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDateDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemDtoAllFieldsTest {
    @Autowired
    JacksonTester<ItemDtoAllFields> jacksonTester;

    @Test
    void itemDtoAllFieldsTest() throws IOException {
        BookingDateDto last = BookingDateDto.builder().id(1L).bookerId(14L).start(LocalDateTime.now().minusHours(5))
                .end(LocalDateTime.now().minusHours(1)).build();
        BookingDateDto nearest = BookingDateDto.builder().id(2L).bookerId(15L).start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(5)).build();
        CommentDto commentDto = CommentDto.builder().id(18L).text("хороший").authorName("Марсик")
                .created(LocalDateTime.now()).build();
        ItemDtoAllFields itemDtoAllFields = ItemDtoAllFields.builder().id(13L).name("фен").description("новый")
                .isAvailable(true).ownerId(7L).lastBooking(last).nearestBooking(nearest).comments(List.of(commentDto)).build();

        JsonContent<ItemDtoAllFields> jsonContent = jacksonTester.write(itemDtoAllFields);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(13);
        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo("фен");
        assertThat(jsonContent).extractingJsonPathStringValue("$.description").isEqualTo("новый");
        assertThat(jsonContent).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.ownerId").isEqualTo(7);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(14);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(2);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(15);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(18);
        assertThat(jsonContent).extractingJsonPathStringValue("$.comments[0].text").isEqualTo("хороший");
        assertThat(jsonContent).extractingJsonPathStringValue("$.comments[0].authorName").isEqualTo("Марсик");
    }
}
