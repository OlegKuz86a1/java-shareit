package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingCreateDto {

    @Positive
    private Long itemId;
    @Positive
    private Long bookerId;
    @DateTimeFormat(iso = DATE_TIME)
    @FutureOrPresent(message = "start cannot be earlier present time")
    private LocalDateTime start;
    @DateTimeFormat(iso = DATE_TIME)
    private LocalDateTime end;
}
