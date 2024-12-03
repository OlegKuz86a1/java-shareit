package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserCreateDto;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class UserCreateDtoTest {

    @Autowired
    JacksonTester<UserCreateDto> jacksonTester;

    @Test
    void userCreateDtoTest() throws IOException {
        UserCreateDto userCreateDto = UserCreateDto.builder()
                .name("Test Create")
                .email("testCreate@mail.ru")
                .build();
        JsonContent<UserCreateDto> content = jacksonTester.write(userCreateDto);

        assertThat(content).extractingJsonPathStringValue("$.name").isEqualTo("Test Create");
        assertThat(content).extractingJsonPathStringValue("$.email").isEqualTo("testCreate@mail.ru");


    }
}
