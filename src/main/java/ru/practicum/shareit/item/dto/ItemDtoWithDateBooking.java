package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDateDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDtoWithDateBooking {

        private Long id;
        private String name;
        private String description;
        private boolean isAvailable;
        private Long ownerId;
        private BookingDateDto lastBooking;
        private BookingDateDto nearestBooking;
}
