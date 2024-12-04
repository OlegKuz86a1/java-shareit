package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemCreateDto {

    private String name;
    private String description;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long ownerId;

    @JsonProperty("available")
    private Boolean isAvailable;
    private Long requestId;
}
