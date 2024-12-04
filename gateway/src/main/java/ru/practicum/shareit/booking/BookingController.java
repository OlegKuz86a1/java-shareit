package ru.practicum.shareit.booking;

import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.status.BookingStatus;



@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	private static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";

	@PostMapping
	ResponseEntity<Object> create(@Positive @RequestHeader(SHARER_USER_ID_HEADER) Long bookerId,
								  @Valid @RequestBody BookingCreateDto bookingCreateDto) {
		log.info("received booking for create {}, userId {} ", bookingCreateDto, bookerId);
		return bookingClient.create(bookerId, bookingCreateDto);
	}

	@PatchMapping("/{bookingId}")
	ResponseEntity<Object> update(@Positive @RequestHeader(SHARER_USER_ID_HEADER) Long userId,
								  @Positive @PathVariable Long bookingId, @RequestParam boolean approved) {
		log.info("received PATCH request for user {}: booking={}, approved={}", userId, bookingId, approved);
		return bookingClient.update(userId, bookingId, approved);
	}

	@GetMapping("/{bookingId}")
	ResponseEntity<Object> getByBookingId(@Positive @RequestHeader(SHARER_USER_ID_HEADER) Long userId,
										  @Positive @PathVariable Long bookingId) {
		log.info("received the booking id {} and user id {} for rent before query: ", bookingId, userId);
		return bookingClient.getBooking(userId, bookingId);
	}

	@GetMapping
	ResponseEntity<Object> getAllByUser(@Positive @RequestHeader(SHARER_USER_ID_HEADER) Long userId,
								  @RequestParam(defaultValue = "ALL") BookingStatus state,
								  @RequestParam(defaultValue = "0") @PositiveOrZero int from,
								  @Positive @RequestParam(defaultValue = "10") int size) {
		log.info("received the booking id get by user {}, state {}, from {}, size{} for rent before query: ",
				userId, state, from, size);
		return bookingClient.getAllByUser(userId, state, from, size);
	}

	@GetMapping("/owner")
	ResponseEntity<Object> getAllByOwner(@Positive @RequestHeader(SHARER_USER_ID_HEADER) Long userId,
								   @RequestParam(defaultValue = "ALL") BookingStatus state,
								   @RequestParam(defaultValue = "0") @PositiveOrZero int from,
								   @Positive @RequestParam(defaultValue = "10") int size) {
		log.info("received the booking id get by owner {}, state {}, from {}, size{} for rent before query: ",
				userId, state, from, size);
		return bookingClient.getAllByOwner(userId, state, from, size);
	}
}
