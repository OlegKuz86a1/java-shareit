package ru.practicum.shareit.booking;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.common.exception.AccessDeniedException;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Spy
    protected BookingMapper bookingMapper;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingValidator bookingValidator;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private BookingService bookingService;

    private static BookingCreateDto bookingCreateDto;
    private static BookingDto bookingDto;
    private static Booking booking;
    private static Item item;
    private static ItemRequest itemRequest;
    private static User user;


    @BeforeAll
    static void init() {
        bookingCreateDto = BookingCreateDto.builder().itemId(1L).bookerId(7L).status(BookingStatus.WAITING)
                .start(LocalDateTime.now().minusHours(2L)).end(LocalDateTime.now().plusDays(3L)).build();
        user = User.builder().id(7L).name("Люся").email("luysa@mail.ru").build();
        itemRequest = ItemRequest.builder().id(1L).requestor(user).description("нужно").build();
        item = Item.builder().id(bookingCreateDto.getItemId()).name("мышка").description("HP").isAvailable(true)
                .itemRequest(itemRequest).owner(user).build();
        booking = Booking.builder().id(1L).start(LocalDateTime.now().plusHours(2L)).end(LocalDateTime.now().plusDays(3L))
                .status(BookingStatus.WAITING).booker(user).item(item).build();
    }

    @Test
    void whenAddValidBookingThenCallSaveBookingRepository() {
        when(bookingRepository.saveAndFlush(bookingMapper.toEntity(bookingCreateDto))).thenReturn(booking);
        BookingDto returnDto = bookingService.addBooking(bookingCreateDto);

        assertThat(returnDto, equalTo(bookingMapper.toDto(booking)));
        verify(bookingRepository, times(1)).saveAndFlush(bookingMapper.toEntity(bookingCreateDto));
    }


    @Test
    void changeApprovedByNotExistsBookingThenNotFound() {
        when(bookingRepository.findById(3L)).thenThrow(new NotFoundException("Booking not found with id: 3"));
        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.changeApproved(1L, 3L, true));

        assertEquals("Booking not found with id: 3", exception.getMessage());
        verify(bookingRepository, times(1)).findById(anyLong());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void changeApprovedSomeoneElseBookingThenAccessDenied() {
        when(bookingRepository.findById(3L)).thenReturn(Optional.of(booking));
        final AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> bookingService.changeApproved(1L, 3L, true));

        assertEquals("booking with id=3 for the user with id=1 was not found", exception.getMessage());
        verify(bookingRepository, times(1)).findById(anyLong());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void changeApprovedStatusValidBookingThenSaveBookingRepository() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        bookingService.changeApproved(7L, 1L, true);

        verify(bookingRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void getByIdNotExistsBookingThenEntityNotFound() {
        when(bookingRepository.findById(7L)).thenThrow(new EntityNotFoundException("Booking with id=7 not found"));
        final EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> bookingService.getById(3L, 7L));

        assertEquals("Booking with id=7 not found", exception.getMessage());
        verify(bookingRepository, times(1)).findById(anyLong());
    }

    @Test
    void getCorrectBookingByIdThenReturnBookingDto() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        BookingDto findDto = bookingService.getById(1L, 1L);

        assertThat(findDto, equalTo(bookingMapper.toDto(booking)));
        verify(bookingRepository, times(1)).findById(anyLong());
    }

    @Test
    void getAllByUserThenCallGetAllByBookerIdBookingRepository() {
        Page<Booking> bookings = new PageImpl<>(List.of(booking));
        when(bookingRepository.getAllByBookerId(anyLong(), any(Pageable.class))).thenReturn(bookings);
        bookingService.getAllByUser(1L, BookingStatus.ALL, 1, 10);

        verify(bookingRepository, times(1)).getAllByBookerId(anyLong(), any(Pageable.class));
        verify(bookingRepository, never()).getAllCurrentBookingByBookerId(anyLong(), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).getAllByBookerIdAndEndBefore(anyLong(), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).getAllByBookerIdAndStartAfter(anyLong(), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).getAllByBookerIdAndStatus(anyLong(), any(), any(Pageable.class));

    }

    @Test
    void getAllByUserAndStatePastThenCallGetAllByBookerIdAndEndBeforeBookingRepository() {
        Page<Booking> bookings = new PageImpl<>(List.of(booking));
        when(bookingRepository.getAllByBookerIdAndEndBefore(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(bookings);
        bookingService.getAllByUser(1L, BookingStatus.PAST, 1, 10);

        verify(bookingRepository, times(1)).getAllByBookerIdAndEndBefore(anyLong(),
                any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).getAllByBookerId(anyLong(), any(Pageable.class));
        verify(bookingRepository, never()).getAllCurrentBookingByBookerId(anyLong(), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).getAllByBookerIdAndStartAfter(anyLong(), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).getAllByBookerIdAndStatus(anyLong(), any(), any(Pageable.class));
    }

    @Test
    void getAllByUserAndStateCurrentThenCallGetAllCurrentBookingByBookerIdBookingRepository() {
        Page<Booking> bookings = new PageImpl<>(List.of(booking));
        when(bookingRepository.getAllCurrentBookingByBookerId(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(bookings);
        bookingService.getAllByUser(1L, BookingStatus.CURRENT, 1, 10);

        verify(bookingRepository, times(1)).getAllCurrentBookingByBookerId(anyLong(),
                any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).getAllByBookerId(anyLong(), any(Pageable.class));
        verify(bookingRepository, never()).getAllByBookerIdAndEndBefore(anyLong(), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).getAllByBookerIdAndStartAfter(anyLong(), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).getAllByBookerIdAndStatus(anyLong(), any(), any(Pageable.class));
    }

    @Test
    void getAllByUserAndStateFutureThenCallGetAllByBookerIdAndEndAfterBookingRepository() {
        Page<Booking> bookings = new PageImpl<>(List.of(booking));
        when(bookingRepository.getAllByBookerIdAndStartAfter(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(bookings);
        bookingService.getAllByUser(1L, BookingStatus.FUTURE, 1, 10);

        verify(bookingRepository, times(1)).getAllByBookerIdAndStartAfter(anyLong(),
                any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).getAllByBookerId(anyLong(), any(Pageable.class));
        verify(bookingRepository, never()).getAllCurrentBookingByBookerId(anyLong(), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).getAllByBookerIdAndEndBefore(anyLong(), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).getAllByBookerIdAndStatus(anyLong(), any(), any(Pageable.class));
    }

    @Test
    void getAllByUserAndStateWaitingThenCallGetAllByBookerIdAndStatusBookingRepository() {
        Pageable pageable = PageRequest.of(1 / 10, 10, Sort.Direction.DESC, "start");
        Page<Booking> bookings = new PageImpl<>(List.of(booking));
        when(bookingRepository.getAllByBookerIdAndStatus(1L,BookingStatus.WAITING, pageable))
                .thenReturn(bookings);
        bookingService.getAllByUser(1L, BookingStatus.WAITING, 1, 10);

        verify(bookingRepository, times(1)).getAllByBookerIdAndStatus(1L,
                BookingStatus.WAITING, pageable);
        verify(bookingRepository, never()).getAllByBookerId(anyLong(), any(Pageable.class));
        verify(bookingRepository, never()).getAllCurrentBookingByBookerId(anyLong(), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).getAllByBookerIdAndEndBefore(anyLong(), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).getAllByBookerIdAndStartAfter(anyLong(), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllByUserAndStateRejectedThenCallGetAllByBookerIdAndStatusBookingRepository() {
        Pageable pageable = PageRequest.of(1 / 10, 10, Sort.Direction.DESC, "start");
        Page<Booking> bookings = new PageImpl<>(List.of(booking));
        when(bookingRepository.getAllByBookerIdAndStatus(1L,BookingStatus.REJECTED, pageable))
                .thenReturn(bookings);
        bookingService.getAllByUser(1L, BookingStatus.REJECTED, 1, 10);

        verify(bookingRepository, times(1)).getAllByBookerIdAndStatus(1L,
                BookingStatus.REJECTED, pageable);
        verify(bookingRepository, never()).getAllByBookerId(anyLong(), any(Pageable.class));
        verify(bookingRepository, never()).getAllCurrentBookingByBookerId(anyLong(), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).getAllByBookerIdAndEndBefore(anyLong(), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).getAllByBookerIdAndStartAfter(anyLong(), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllByOwnerThenCallGetAllByOwnerIdBookingRepository() {
        Page<Booking> bookings = new PageImpl<>(List.of(booking));
        when(bookingRepository.getAllByOwnerId(anyLong(), any(Pageable.class))).thenReturn(bookings);
        bookingService.getAllByOwner(1L, BookingStatus.ALL, 1, 10);

        verify(bookingRepository, times(1)).getAllByOwnerId(anyLong(), any(Pageable.class));
        verify(bookingRepository, never()).getAllByOwnerIdAndEndBefore(anyLong(), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).getAllCurrentBookingByOwnerId(anyLong(), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).getAllByOwnerIdAndEndAfter(anyLong(), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).getAllByOwnerIdAndStatus(anyLong(), any(), any(Pageable.class));

    }

    @Test
    void getAllByOwnerAndStatusPastThenCallGetAllByOwnerIdBookingRepository() {
        Page<Booking> bookings = new PageImpl<>(List.of(booking));
        when(bookingRepository.getAllByOwnerIdAndEndBefore(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(bookings);
        bookingService.getAllByOwner(1L, BookingStatus.PAST, 1, 10);

        verify(bookingRepository, times(1)).getAllByOwnerIdAndEndBefore(anyLong(),
                any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).getAllByOwnerId(anyLong(), any(Pageable.class));
        verify(bookingRepository, never()).getAllCurrentBookingByOwnerId(anyLong(), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).getAllByOwnerIdAndEndAfter(anyLong(), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).getAllByOwnerIdAndStatus(anyLong(), any(), any(Pageable.class));

    }

    @Test
    void getAllByOwnerAndStatusCurrentThenCallGetAllCurrentBookingByOwnerIdBookingRepository() {
        Page<Booking> bookings = new PageImpl<>(List.of(booking));
        when(bookingRepository.getAllCurrentBookingByOwnerId(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(bookings);
        bookingService.getAllByOwner(1L, BookingStatus.CURRENT, 1, 10);

        verify(bookingRepository, times(1)).getAllCurrentBookingByOwnerId(anyLong(),
                any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).getAllByOwnerId(anyLong(), any(Pageable.class));
        verify(bookingRepository, never()).getAllByOwnerIdAndEndBefore(anyLong(), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).getAllByOwnerIdAndEndAfter(anyLong(), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).getAllByOwnerIdAndStatus(anyLong(), any(), any(Pageable.class));

    }

    @Test
    void getAllByOwnerAndStatusFutureThenCallGetAllByOwnerIdAndEndAfterBookingRepository() {
        Page<Booking> bookings = new PageImpl<>(List.of(booking));
        when(bookingRepository.getAllByOwnerIdAndEndAfter(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(bookings);
        bookingService.getAllByOwner(1L, BookingStatus.FUTURE, 1, 10);

        verify(bookingRepository, times(1)).getAllByOwnerIdAndEndAfter(anyLong(),
                any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).getAllByOwnerId(anyLong(), any(Pageable.class));
        verify(bookingRepository, never()).getAllByOwnerIdAndEndBefore(anyLong(), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).getAllCurrentBookingByOwnerId(anyLong(), any(LocalDateTime.class), any(Pageable.class));
        verify(bookingRepository, never()).getAllByOwnerIdAndStatus(anyLong(), any(), any(Pageable.class));

    }


}
