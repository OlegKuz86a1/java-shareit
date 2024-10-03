package ru.practicum.shareit.common;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseEntity {

    private Long id;
}
