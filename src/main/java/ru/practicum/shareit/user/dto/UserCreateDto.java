package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @Email
    @NotNull
    private String email;

    @NotNull
    @NotBlank
    private String name;
}
