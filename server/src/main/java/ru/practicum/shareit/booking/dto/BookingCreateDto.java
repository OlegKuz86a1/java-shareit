package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingCreateDto {

    private Long itemId;
    private Long bookerId;
    @DateTimeFormat(iso = DATE_TIME)
    private LocalDateTime start;
    @DateTimeFormat(iso = DATE_TIME)
    private LocalDateTime end;
    private BookingStatus status;
}
