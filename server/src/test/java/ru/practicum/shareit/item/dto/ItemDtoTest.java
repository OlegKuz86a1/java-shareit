package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemDtoTest {
    @Autowired
    JacksonTester<ItemDto> jacksonTester;

    @Test
    void itemDtoTest() throws IOException {
        ItemDto itemDto = ItemDto.builder().id(13L).name("фен").description("новый").isAvailable(true).ownerId(7L)
                .requestId(8L).build();
        JsonContent<ItemDto> jsonContent = jacksonTester.write(itemDto);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(13);
        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo("фен");
        assertThat(jsonContent).extractingJsonPathStringValue("$.description").isEqualTo("новый");
        assertThat(jsonContent).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.ownerId").isEqualTo(7);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.requestId").isEqualTo(8);
    }
}
