package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemRequestCreateTest {
    @Autowired
    JacksonTester<ItemRequestCreate> jacksonTester;

    @Test
    void testItemRequestCreate() throws IOException {
        LocalDateTime created = LocalDateTime.now();
        ItemRequestCreate itemRequestDto = ItemRequestCreate.builder().description("нужен кот")
                .dateRequestCreated(created).requestor(7L).build();
        JsonContent<ItemRequestCreate> dtoJsonContent = jacksonTester.write(itemRequestDto);

        assertThat(dtoJsonContent).extractingJsonPathNumberValue("$.requestor").isEqualTo(7);
        assertThat(dtoJsonContent).extractingJsonPathStringValue("$.description").isEqualTo("нужен кот");
    }
}
