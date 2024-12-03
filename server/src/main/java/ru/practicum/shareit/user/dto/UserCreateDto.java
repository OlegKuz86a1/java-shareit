package ru.practicum.shareit.user.dto;

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
public class UserCreateDto implements UserBaseGetter {

    private String email;
    private String name;
}
