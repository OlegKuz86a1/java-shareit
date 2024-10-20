package ru.practicum.shareit.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.common.BaseEntity;

@Entity
@Table(name = "users")
@Setter
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {

        @Column(length = 128, nullable = false)
        private String name;
        @Column(length = 128, unique = true)
        private String email;

}
