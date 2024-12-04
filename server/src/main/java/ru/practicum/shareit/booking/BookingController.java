package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    private static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    BookingDto create(@RequestHeader(SHARER_USER_ID_HEADER) Long bookerId,
                       @RequestBody BookingCreateDto bookingCreateDto) {
        log.info("returning the booking {} and booker {} for rent before query: ", bookingCreateDto, bookerId);
        bookingCreateDto.setBookerId(bookerId);
        BookingDto bookingDto = bookingService.addBooking(bookingCreateDto);
        log.info("returning the booking for rent after query: {}", bookingDto);
        return bookingDto;
    }

    @PatchMapping("/{bookingId}")
    BookingDto update(@RequestHeader(SHARER_USER_ID_HEADER) Long userId,
                       @PathVariable Long bookingId, @RequestParam boolean approved) {
        log.info("Received PATCH request for user {}: booking={}, approved={}",
                userId, bookingId, approved);
        BookingDto bookingDto = bookingService.changeApproved(userId, bookingId, approved);
        log.info("returning the update booking for rent after query: {}", bookingDto);
        return bookingDto;
    }

    @GetMapping("/{bookingId}")
    BookingDto getByBookingId(@RequestHeader(SHARER_USER_ID_HEADER) Long userId,
                       @PathVariable Long bookingId) {
        log.info("returning the booking id {} and user id {} for rent before query: ", bookingId, userId);
        BookingDto byId = bookingService.getById(userId, bookingId);
        log.info("returning the booking get by id for rent after query: {}", byId);
        return byId;
    }

    @GetMapping
    List<BookingDto> getAllByUser(@RequestHeader(SHARER_USER_ID_HEADER) Long userId,
                                  @RequestParam(defaultValue = "ALL") BookingStatus state,
                                  @RequestParam(defaultValue = "1") int from,
                                  @RequestParam(defaultValue = "10") int size) {
        log.info("returning the booking id get by user for rent before query: {}", userId);
        List<BookingDto> allByUser = bookingService.getAllByUser(userId, state, from, size);
        log.info("returning the booking get by user for rent after query: {}", allByUser);
        return allByUser;
   }

    @GetMapping("/owner")
    List<BookingDto> getAllByOwner(@RequestHeader(SHARER_USER_ID_HEADER) Long userId,
                                   @RequestParam(defaultValue = "ALL") BookingStatus state,
                                   @RequestParam(defaultValue = "1") int from,
                                   @RequestParam(defaultValue = "10") int size) {
        log.info("returning the booking id get by owner for rent before query: {}", userId);
        List<BookingDto> allByOwner = bookingService.getAllByOwner(userId, state, from, size);
        log.info("returning the booking get by owner for rent after query: {}", allByOwner);
        return allByOwner;
    }
}
