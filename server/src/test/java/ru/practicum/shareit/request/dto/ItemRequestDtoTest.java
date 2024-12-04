package ru.practicum.shareit.request.dto;

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
public class ItemRequestDtoTest {

    @Autowired
    JacksonTester<ItemRequestDto> jacksonTester;

    @Test
    void testItemRequestDto() throws IOException {
        LocalDateTime created = LocalDateTime.now();
        ItemRequestDto itemRequestDto = ItemRequestDto.builder().id(8L).description("нужен кот")
                .dateRequestCreated(created).requestor(7L).build();
        JsonContent<ItemRequestDto> dtoJsonContent = jacksonTester.write(itemRequestDto);

        assertThat(dtoJsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(8);
        assertThat(dtoJsonContent).extractingJsonPathNumberValue("$.requestor").isEqualTo(7);
        assertThat(dtoJsonContent).extractingJsonPathStringValue("$.description").isEqualTo("нужен кот");
        assertThat(dtoJsonContent).extractingJsonPathStringValue("$.created")
                .isEqualTo(created.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

    }

}
