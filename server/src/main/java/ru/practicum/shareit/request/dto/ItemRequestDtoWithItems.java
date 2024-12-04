package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequestDtoWithItems {

    private Long id;
    private String description;
    private Long requestor;
    @DateTimeFormat(iso = DATE_TIME)
    @JsonProperty(value = "created")
    private LocalDateTime dateRequestCreated;
    private List<ItemDto> items;

}
