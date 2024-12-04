package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemRequestDtoWithItemsTest {

    @Autowired
    JacksonTester<ItemRequestDtoWithItems> jacksonTester;

    @Test
    void testItemRequestDtoWithItems() throws IOException {
        ItemDto itemDto1 = ItemDto.builder().id(1L).name("Красавчик").description("Рыжий кот").isAvailable(true)
                .ownerId(7L).requestId(8L).build();
        ItemDto itemDto2 = ItemDto.builder().id(2L).name("Счастливчик").description("Кот толстячек").isAvailable(true)
                .ownerId(6L).requestId(8L).build();

        LocalDateTime created = LocalDateTime.now();
        ItemRequestDtoWithItems itemRequestDtoWithItems = ItemRequestDtoWithItems.builder().id(8L).description("нужен кот")
                .dateRequestCreated(created).requestor(7L).items(List.of(itemDto1, itemDto2)).build();

        JsonContent<ItemRequestDtoWithItems> dtoJsonContent = jacksonTester.write(itemRequestDtoWithItems);

        assertThat(dtoJsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(8);
        assertThat(dtoJsonContent).extractingJsonPathNumberValue("$.requestor").isEqualTo(7);
        assertThat(dtoJsonContent).extractingJsonPathStringValue("$.description").isEqualTo("нужен кот");
        assertThat(dtoJsonContent).extractingJsonPathStringValue("$.created")
                .isEqualTo(created.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        assertThat(dtoJsonContent).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(1);
        assertThat(dtoJsonContent).extractingJsonPathNumberValue("$.items[0].requestId").isEqualTo(8);
        assertThat(dtoJsonContent).extractingJsonPathStringValue("$.items[0].description").isEqualTo("Рыжий кот");
        assertThat(dtoJsonContent).extractingJsonPathBooleanValue("$.items[0].available").isEqualTo(true);
        assertThat(dtoJsonContent).extractingJsonPathNumberValue("$.items[0].ownerId").isEqualTo(7);

        assertThat(dtoJsonContent).extractingJsonPathNumberValue("$.items[1].id").isEqualTo(2);
        assertThat(dtoJsonContent).extractingJsonPathNumberValue("$.items[1].requestId").isEqualTo(8);
        assertThat(dtoJsonContent).extractingJsonPathStringValue("$.items[1].description").isEqualTo("Кот толстячек");
        assertThat(dtoJsonContent).extractingJsonPathBooleanValue("$.items[1].available").isEqualTo(true);
        assertThat(dtoJsonContent).extractingJsonPathNumberValue("$.items[1].ownerId").isEqualTo(6);

    }

}
