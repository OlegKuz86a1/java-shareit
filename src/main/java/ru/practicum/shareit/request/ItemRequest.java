package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class ItemRequest {

    private Long id;
    private String description;
    private Long requestor;
    private LocalDateTime dateRequestCreated;
}
