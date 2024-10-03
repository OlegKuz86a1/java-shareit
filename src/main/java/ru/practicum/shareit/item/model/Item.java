package ru.practicum.shareit.item.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.common.BaseEntity;

@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class Item extends BaseEntity {

    private String name;
    private String description;
    private boolean isAvailable;
    private Long ownerId;
    private Long requestId;
}
