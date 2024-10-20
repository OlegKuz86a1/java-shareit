package ru.practicum.shareit.common;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@Setter
@Getter
@ToString
@EqualsAndHashCode
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
