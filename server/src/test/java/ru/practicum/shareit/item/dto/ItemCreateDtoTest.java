package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemCreateDtoTest {
    @Autowired
    JacksonTester<ItemCreateDto> jacksonTester;

    @Test
    void itemCreateDtoTest() throws IOException {
        ItemCreateDto itemCreateDto = ItemCreateDto.builder().name("фен").description("новый").isAvailable(true)
                .ownerId(7L).requestId(8L).build();
        JsonContent<ItemCreateDto> jsonContent = jacksonTester.write(itemCreateDto);

        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo("фен");
        assertThat(jsonContent).extractingJsonPathStringValue("$.description").isEqualTo("новый");
        assertThat(jsonContent).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.requestId").isEqualTo(8);

    }
}
