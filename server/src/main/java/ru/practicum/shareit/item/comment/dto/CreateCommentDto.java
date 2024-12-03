package ru.practicum.shareit.item.comment.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateCommentDto {
    private String text;
    private Long itemId;
    private Long authorId;

}