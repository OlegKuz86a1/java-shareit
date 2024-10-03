package ru.practicum.shareit.user.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Getter
@Setter
@Data
@NoArgsConstructor
public class UserDto extends UserCreateDto {

    private Long id;
}
