package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.common.BaseEntity;
import ru.practicum.shareit.user.model.User;

@Entity
@Table(name = "items")
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@NamedEntityGraph(name = "item.owner", attributeNodes = {
        @NamedAttributeNode("owner")
})
public class Item extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(name = "available", nullable = false)
    private boolean isAvailable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;
}
