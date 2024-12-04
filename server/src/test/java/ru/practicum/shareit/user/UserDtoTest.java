package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@JsonTest
public class UserDtoTest {
    @Autowired
    JacksonTester<UserDto> jacksonTester;

    @Test
    void testUserDto() throws IOException {
        UserDto userDto = UserDto.builder().id(7L).name("Марсик").email("marsik@mail.ru").build();
        JsonContent<UserDto> jsonContent = jacksonTester.write(userDto);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(7);
        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo("Марсик");
        assertThat(jsonContent).extractingJsonPathStringValue("$.email").isEqualTo("marsik@mail.ru");

    }
}
