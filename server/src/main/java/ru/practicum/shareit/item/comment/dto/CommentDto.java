package ru.practicum.shareit.item.comment.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    private Long id;
    private String text;
    private ItemDto item;
    private String authorName;
    private LocalDateTime created;
}
