package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemCreateDto {

    @NotBlank(message = "The item name cannot be empty")
    private String name;

    @NotBlank(message = "The description cannot be empty")
    private String description;

    @JsonIgnore
    private Long ownerId;

    @NotNull(message = "The available status cannot be empty")
    @JsonProperty("available")
    private Boolean isAvailable;
}
