package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemCreateDto;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemCreateDtoTest {

    @Autowired
    JacksonTester<ItemCreateDto> jacksonTester;

    @Test
    void correctItem() throws IOException {
        ItemCreateDto itemCreateDto = ItemCreateDto.builder().name("фотоаппарат").description("профессиональный, Nikon")
                .ownerId(2L).isAvailable(true).requestId(1L).build();
        JsonContent<ItemCreateDto> jsonContent = jacksonTester.write(itemCreateDto);

        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo("фотоаппарат");
        assertThat(jsonContent).extractingJsonPathStringValue("$.description")
                .isEqualTo("профессиональный, Nikon");
        assertThat(jsonContent).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }


}
