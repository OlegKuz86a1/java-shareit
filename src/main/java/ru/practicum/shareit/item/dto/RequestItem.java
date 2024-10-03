package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RequestItem {

    @JsonIgnore
    private Long id;
    private String name;
    private String description;
    @JsonProperty("available")
    private Boolean isAvailable;

}
