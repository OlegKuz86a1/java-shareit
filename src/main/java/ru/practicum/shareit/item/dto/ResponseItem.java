package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class ResponseItem {
    private String name;
    private String description;
    private boolean isAvailable;
}
