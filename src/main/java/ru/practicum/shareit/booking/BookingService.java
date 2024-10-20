package ru.practicum.shareit.booking;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.common.exception.AccessDeniedException;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@EnableTransactionManagement
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final BookingValidator bookingValidator;
    private final UserRepository userRepository;
    private final EntityManager em;

    public BookingDto addBooking(BookingCreateDto bookingCreateDto) {
        bookingValidator.validate(bookingCreateDto.getItemId(), bookingCreateDto.getBookerId());
        bookingCreateDto.setStatus(BookingStatus.WAITING);
        Booking saved = bookingRepository.saveAndFlush(bookingMapper.toEntity(bookingCreateDto));
        bookingRepository.flush();
        return bookingMapper.toDto(findById(saved));
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public Booking findById(Booking id) {
        em.clear();
        Booking found = bookingRepository.findById(id.getId()).get();
        return found;
    }

    public BookingDto changeApproved(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId));

        if (!Objects.equals(userId, booking.getItem().getOwner().getId()))
            throw new AccessDeniedException(String.format("booking with id=%s for the user with id=%s was not found",
                    bookingId, userId));

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    public BookingDto getById(Long ownerId, Long bookingId) {
        bookingValidator.validateBooking(bookingId);
        bookingValidator.validateUser(ownerId);
        return bookingMapper.toDto(bookingRepository.findById(bookingId).orElse(null));
    }




    public List<BookingDto> getAllByUser(Long userId, BookingStatus status, int from, int size) {
        bookingValidator.validateUser(userId);

        Pageable pageable = PageRequest.of(from / size, size, Sort.Direction.DESC, "start");

        Page<Booking> bookings;
        if (status == BookingStatus.ALL) {
            bookings = bookingRepository.getAllByBookerId(userId, pageable);
        } else if (status == BookingStatus.PAST) {
            bookings = bookingRepository.getAllByBookerIdAndEndBefore(userId, LocalDateTime.now(), pageable);
        } else if (status == BookingStatus.CURRENT) {
            bookings = bookingRepository.getAllCurrentBookingByBookerId(userId, LocalDateTime.now(), pageable);
        } else if (status == BookingStatus.FUTURE) {
            bookings = bookingRepository.getAllByBookerIdAndEndAfter(userId, LocalDateTime.now(), pageable);
        } else {
            bookings = bookingRepository.getAllByBookerIdAndStatus(userId,
                    BookingStatus.valueOf(status.name()), pageable);
        }
        return bookings.stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());

    }

    public List<BookingDto> getAllByOwner(Long userId, BookingStatus status, int from, int size) {
        bookingValidator.validateUser(userId);

        Pageable pageable = PageRequest.of(from / size, size, Sort.Direction.DESC, "start");

        Page<Booking> bookings;
        if (Objects.requireNonNull(status) == BookingStatus.ALL) {
            bookings = bookingRepository.getAllByOwnerId(userId, pageable);
        } else if (status == BookingStatus.PAST) {
            bookings = bookingRepository.getAllByOwnerIdAndEndBefore(userId, LocalDateTime.now(), pageable);
        } else if (status == BookingStatus.CURRENT) {
            bookings = bookingRepository.getAllCurrentBookingByOwnerId(userId, LocalDateTime.now(), pageable);
        } else if (status == BookingStatus.FUTURE) {
            bookings = bookingRepository.getAllByOwnerIdAndEndAfter(userId, LocalDateTime.now(), pageable);
        } else {
            bookings = bookingRepository.getAllByOwnerIdAndStatus(userId,
                    BookingStatus.valueOf(status.name()), pageable);
        }
        return bookings.stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }
}
