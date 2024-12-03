package ru.practicum.shareit.booking;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingValidatorTest {
    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingValidator bookingValidator;

    @Test
    void validateBookingTest() {
        when(bookingRepository.findById(7L)).thenThrow(new EntityNotFoundException("Booking with id=7 not found"));
        final EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> bookingValidator.validateBooking(7L));

        assertEquals("Booking with id=7 not found", exception.getMessage());
        verify(bookingRepository, times(1)).findById(anyLong());
    }

    @Test
    void validateItemTest() {
        when(itemRepository.findById(7L)).thenThrow(new NotFoundException("Item with id=7 not found"));
        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingValidator.validateItem(7L));

        assertEquals("Item with id=7 not found", exception.getMessage());
        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void validateUserTest() {
        when(userRepository.findById(7L)).thenThrow(new NotFoundException("User with id=7 not found"));
        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingValidator.validateUser(7L));

        assertEquals("User with id=7 not found", exception.getMessage());
        verify(userRepository, times(1)).findById(anyLong());
    }

}
