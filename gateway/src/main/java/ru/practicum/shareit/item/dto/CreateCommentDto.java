package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateCommentDto {

    @NotNull(message = "Text must not be null")
    @NotBlank(message = "Text must not be blank")
    private String text;
    private Long itemId;
    private Long authorId;

}