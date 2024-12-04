package ru.practicum.shareit.request.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequestDto  {

    private Long id;
    private String description;
    private Long requestor;
    @JsonProperty(value = "created")
    private LocalDateTime dateRequestCreated;
    private Item item;
}
