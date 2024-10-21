package ru.practicum.shareit.item.comment.dto;

import lombok.Data;

@Data
public class CreateCommentDto {
    private String text;
    private Long itemId;
    private Long authorId;

}