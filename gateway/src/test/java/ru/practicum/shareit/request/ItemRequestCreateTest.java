package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestCreate;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemRequestCreateTest {

    @Autowired
    JacksonTester<ItemRequestCreate> itemRequestCreateJacksonTester;

    @Test
    void correctItemRequest() throws IOException {
        ItemRequestCreate itemRequestCreate = ItemRequestCreate.builder().description("нужен фотоаппарат").build();
        JsonContent<ItemRequestCreate> jsonContent = itemRequestCreateJacksonTester.write(itemRequestCreate);

        assertThat(jsonContent).extractingJsonPathStringValue("$.description").isEqualTo("нужен фотоаппарат");
    }
}
