package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDateDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDtoAllFields {
    private Long id;
    private String name;
    private String description;
    private boolean isAvailable;
    private Long ownerId;
    private BookingDateDto lastBooking;
    @JsonProperty(value = "nextBooking")
    private BookingDateDto nearestBooking;
    private List<CommentDto> comments;


}
