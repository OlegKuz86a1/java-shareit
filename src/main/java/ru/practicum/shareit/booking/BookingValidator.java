package ru.practicum.shareit.booking;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.common.exception.BookingFailedException;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class BookingValidator {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    public void validate(Long itemId) {
        if (!validateItem(itemId).isAvailable()) {
            throw new BookingFailedException(String.format("Item with id=%s is not available", itemId));
        }
    }

    public void validate(Long itemId, Long userId) {
        validateUser(userId);
        if (!validateItem(itemId).isAvailable()) {
            throw new BookingFailedException(String.format("Item with id=%s is not available", itemId));
        }
    }

    public void validateBooking(Long bookingId) {
        bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Booking with id=%s not found",bookingId)));

    }

    public Item validateItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item with id=%s not found",itemId)));

        }

    public void validateUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%s not found",userId)));
    }
}
