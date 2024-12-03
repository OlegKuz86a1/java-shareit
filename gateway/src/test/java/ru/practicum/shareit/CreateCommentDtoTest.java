package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CreateCommentDto;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class CreateCommentDtoTest {
    @Autowired
    JacksonTester<CreateCommentDto> jacksonTester;

    @Test
    void correctComment() throws IOException {
        CreateCommentDto createCommentDto = CreateCommentDto.builder().text("хороший фотоаппарат").itemId(1L)
                .authorId(2L).build();
        JsonContent<CreateCommentDto> jsonContent = jacksonTester.write(createCommentDto);

        assertThat(jsonContent).extractingJsonPathStringValue("$.text").isEqualTo("хороший фотоаппарат");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.authorId").isEqualTo(2);
    }
}
