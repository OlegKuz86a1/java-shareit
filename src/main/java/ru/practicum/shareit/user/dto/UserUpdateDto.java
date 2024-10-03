package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@Data
@NoArgsConstructor
public class UserUpdateDto implements UserBaseGetter {

    @Email
    private String email;

    private String name;
}
