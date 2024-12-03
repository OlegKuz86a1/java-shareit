package ru.practicum.shareit.booking;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(properties = "db.name=test", webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceDBTransactionalTest {
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final EntityManager entityManager;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    private UserCreateDto itemOwner;
    private UserCreateDto itemBooker;
    private ItemCreateDto item;
    private BookingCreateDto booking;

    @BeforeEach
    void init() {
        itemOwner = UserCreateDto.builder().name("Люся").email("lusya@mail.ru").build();
        itemBooker = UserCreateDto.builder().name("Марсик").email("marsik@mail.ru").build();
        item = ItemCreateDto.builder().name("мышка").description("HP").isAvailable(true).build();
        booking = BookingCreateDto.builder().start(LocalDateTime.now().minusDays(1L))
                .end(LocalDateTime.now().plusDays(1L)).build();
    }

    @Test
    void addBookingTest() {
        UserDto ownerDto = userService.create(itemOwner);
        item.setOwnerId(ownerDto.getId());
        ItemDto itemDto = itemService.addItem(item);
        UserDto bookerDto = userService.create(itemBooker);
        booking.setItemId(itemDto.getId());
        booking.setBookerId(bookerDto.getId());

        BookingDto bookingDto = bookingService.addBooking(booking);
        TypedQuery<Booking> query = entityManager
                .createQuery("Select b from Booking b where b.id = :bookingId", Booking.class);
        Booking booking = query.setParameter("bookingId", bookingDto.getId()).getSingleResult();

        assertThat(bookingDto.getId(), notNullValue());
        assertThat(bookingDto.getItem(), equalTo(itemDto));
        assertThat(bookingDto.getBooker(), equalTo(bookerDto));
        assertThat(bookingDto.getStatus(), equalTo(booking.getStatus()));
    }

    @Test
    void getByIdTest() {
        UserDto ownerDto = userService.create(itemOwner);
        item.setOwnerId(ownerDto.getId());

        ItemDto itemDto = itemService.addItem(item);
        UserDto bookerDto = userService.create(itemBooker);

        booking.setItemId(itemDto.getId());
        booking.setBookerId(bookerDto.getId());
        BookingDto bookingDto = bookingService.addBooking(booking);

        BookingDto bookingReturn = bookingService.getById(ownerDto.getId(), bookingDto.getId());
        TypedQuery<Booking> query = entityManager
                .createQuery("Select b from Booking b where b.id = :bookingId", Booking.class);
        Booking booking = query.setParameter("bookingId", bookingReturn.getId()).getSingleResult();

        assertThat(bookingReturn.getBooker().getId(), equalTo(booking.getBooker().getId()));
        assertThat(bookingReturn.getItem().getId(), equalTo(booking.getItem().getId()));
        assertThat(bookingReturn.getStart(), equalTo(booking.getStart()));
        assertThat(bookingReturn.getEnd(), equalTo(booking.getEnd()));
        assertThat(bookingReturn.getStatus(), equalTo(booking.getStatus()));
    }

    @Test
    void getAllByUserTest() {
        UserDto ownerDto = userService.create(itemOwner);
        item.setOwnerId(ownerDto.getId());
        ItemDto itemDto = itemService.addItem(item);
        UserDto bookerDto = userService.create(itemBooker);

        booking.setItemId(itemDto.getId());
        booking.setBookerId(bookerDto.getId());
        BookingDto firstBooking = bookingService.addBooking(booking);

        BookingCreateDto oneMoreBooking = BookingCreateDto.builder().start(LocalDateTime.now().minusHours(8L))
                .end(LocalDateTime.now().plusDays(7L)).build();
        oneMoreBooking.setItemId(itemDto.getId());
        oneMoreBooking.setBookerId(bookerDto.getId());

        BookingDto secondBooking = bookingService.addBooking(oneMoreBooking);
        List<BookingDto> bookingReturn = bookingService.getAllByUser(bookerDto.getId(), BookingStatus.ALL, 1, 10);

        assertThat(bookingReturn.size(), equalTo(2));
        assertThat(bookingReturn.get(0).getBooker().getId(), equalTo(firstBooking.getBooker().getId()));
        assertThat(bookingReturn.get(0).getItem().getId(), equalTo(firstBooking.getItem().getId()));
        assertThat(bookingReturn.get(1).getBooker().getId(), equalTo(secondBooking.getBooker().getId()));
        assertThat(bookingReturn.get(1).getItem().getId(), equalTo(secondBooking.getItem().getId()));
    }

    @Test
    void getAllByUserAndStatusPastTest() throws InterruptedException {
        UserDto ownerDto = userService.create(itemOwner);
        item.setOwnerId(ownerDto.getId());
        ItemDto itemDto = itemService.addItem(item);
        UserDto bookerDto = userService.create(itemBooker);

        booking.setItemId(itemDto.getId());
        booking.setBookerId(bookerDto.getId());
        BookingDto firstBooking = bookingService.addBooking(booking);

        BookingCreateDto oneMoreBooking = BookingCreateDto.builder().start(LocalDateTime.now().minusDays(1L))
                .end(LocalDateTime.now().minusHours(2L)).build();
        oneMoreBooking.setItemId(itemDto.getId());
        oneMoreBooking.setBookerId(bookerDto.getId());
        BookingDto secondBooking = bookingService.addBooking(oneMoreBooking);

        List<BookingDto> bookingReturn = bookingService.getAllByUser(bookerDto.getId(), BookingStatus.PAST, 1, 10);

        assertThat(bookingReturn.size(), equalTo(1));
        assertThat(bookingReturn.getFirst().getBooker().getId(), equalTo(secondBooking.getBooker().getId()));
        assertThat(bookingReturn.getFirst().getItem().getId(), equalTo(secondBooking.getItem().getId()));
    }

    @Test
    void getAllByUserAndStatusCurrentTest() throws InterruptedException {
        UserDto ownerDto = userService.create(itemOwner);
        item.setOwnerId(ownerDto.getId());
        ItemDto itemDto = itemService.addItem(item);
        UserDto bookerDto = userService.create(itemBooker);

        booking.setItemId(itemDto.getId());
        booking.setBookerId(bookerDto.getId());
        BookingDto firstBooking = bookingService.addBooking(booking);

        BookingCreateDto oneMoreBooking = BookingCreateDto.builder().start(LocalDateTime.now().plusSeconds(1L))
                .end(LocalDateTime.now().plusSeconds(2L)).build();
        oneMoreBooking.setItemId(itemDto.getId());
        oneMoreBooking.setBookerId(bookerDto.getId());
        BookingDto secondBooking = bookingService.addBooking(oneMoreBooking);

        List<BookingDto> bookingReturn = bookingService.getAllByUser(bookerDto.getId(), BookingStatus.CURRENT, 1, 10);

        assertThat(bookingReturn.size(), equalTo(1));
        assertThat(bookingReturn.getFirst().getBooker().getId(), equalTo(secondBooking.getBooker().getId()));
        assertThat(bookingReturn.getFirst().getItem().getId(), equalTo(secondBooking.getItem().getId()));
    }

    @Test
    void getAllByUserAndStatusFutureTest() throws InterruptedException {
        UserDto ownerDto = userService.create(itemOwner);
        item.setOwnerId(ownerDto.getId());
        ItemDto itemDto = itemService.addItem(item);
        UserDto bookerDto = userService.create(itemBooker);

        booking.setItemId(itemDto.getId());
        booking.setBookerId(bookerDto.getId());
        BookingDto firstBooking = bookingService.addBooking(booking);

        BookingCreateDto oneMoreBooking = BookingCreateDto.builder().start(LocalDateTime.now().plusDays(3L))
                .end(LocalDateTime.now().plusDays(5L)).build();
        oneMoreBooking.setItemId(itemDto.getId());
        oneMoreBooking.setBookerId(bookerDto.getId());
        BookingDto secondBooking = bookingService.addBooking(oneMoreBooking);

        List<BookingDto> bookingReturn = bookingService.getAllByUser(bookerDto.getId(), BookingStatus.FUTURE, 1, 10);
        assertThat(bookingReturn.size(), equalTo(1));
        assertThat(bookingReturn.getFirst().getBooker().getId(), equalTo(secondBooking.getBooker().getId()));
        assertThat(bookingReturn.getFirst().getItem().getId(), equalTo(secondBooking.getItem().getId()));
    }

    @Test
    void getAllByOwnerTest() {
        UserDto ownerDto = userService.create(itemOwner);
        item.setOwnerId(ownerDto.getId());
        item.setOwnerId(ownerDto.getId());
        ItemDto itemDto = itemService.addItem(item);
        UserDto bookerDto = userService.create(itemBooker);

        booking.setItemId(itemDto.getId());
        booking.setBookerId(bookerDto.getId());
        BookingDto firstBooking = bookingService.addBooking(booking);

        BookingCreateDto oneMoreBooking = BookingCreateDto.builder().start(LocalDateTime.now().minusHours(8L))
                .end(LocalDateTime.now().plusDays(7L)).build();
        oneMoreBooking.setItemId(itemDto.getId());
        oneMoreBooking.setBookerId(bookerDto.getId());

        BookingDto secondBooking = bookingService.addBooking(oneMoreBooking);
        List<BookingDto> bookingReturn = bookingService.getAllByOwner(ownerDto.getId(), BookingStatus.ALL, 1, 10);

        assertThat(bookingReturn.size(), equalTo(2));
        assertThat(bookingReturn.get(0).getItem().getOwnerId(), equalTo(firstBooking.getItem().getOwnerId()));
        assertThat(bookingReturn.get(1).getItem().getOwnerId(), equalTo(secondBooking.getItem().getOwnerId()));
    }

    @Test
    void getAllByOwnerAndWaitingTest() {
        UserDto ownerDto = userService.create(itemOwner);
        item.setOwnerId(ownerDto.getId());
        item.setOwnerId(ownerDto.getId());
        ItemDto itemDto = itemService.addItem(item);
        UserDto bookerDto = userService.create(itemBooker);

        booking.setItemId(itemDto.getId());
        booking.setBookerId(bookerDto.getId());
        BookingDto firstBooking = bookingService.addBooking(booking);

        BookingCreateDto oneMoreBooking = BookingCreateDto.builder().start(LocalDateTime.now().minusHours(8L))
                .end(LocalDateTime.now().plusDays(7L)).build();
        oneMoreBooking.setItemId(itemDto.getId());
        oneMoreBooking.setBookerId(bookerDto.getId());

        BookingDto secondBooking = bookingService.addBooking(oneMoreBooking);
        bookingService.changeApproved(ownerDto.getId(), firstBooking.getId(), true);
        List<BookingDto> bookingReturn = bookingService.getAllByOwner(ownerDto.getId(), BookingStatus.WAITING, 1, 10);

        assertThat(bookingReturn.size(), equalTo(1));
        assertThat(bookingReturn.getFirst().getItem().getOwnerId(), equalTo(firstBooking.getItem().getOwnerId()));
        assertThat(bookingReturn.getFirst().getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void getAllByOwnerAndRejectedTest() {
        UserDto ownerDto = userService.create(itemOwner);
        item.setOwnerId(ownerDto.getId());
        item.setOwnerId(ownerDto.getId());
        ItemDto itemDto = itemService.addItem(item);
        UserDto bookerDto = userService.create(itemBooker);

        booking.setItemId(itemDto.getId());
        booking.setBookerId(bookerDto.getId());
        BookingDto firstBooking = bookingService.addBooking(booking);

        BookingCreateDto oneMoreBooking = BookingCreateDto.builder().start(LocalDateTime.now().minusHours(8L))
                .end(LocalDateTime.now().plusDays(7L)).build();
        oneMoreBooking.setItemId(itemDto.getId());
        oneMoreBooking.setBookerId(bookerDto.getId());

        BookingDto secondBooking = bookingService.addBooking(oneMoreBooking);
        bookingService.changeApproved(ownerDto.getId(), secondBooking.getId(), false);
        List<BookingDto> bookingReturn = bookingService.getAllByOwner(ownerDto.getId(), BookingStatus.REJECTED, 1, 10);

        assertThat(bookingReturn.size(), equalTo(1));
        assertThat(bookingReturn.getFirst().getItem().getOwnerId(), equalTo(firstBooking.getItem().getOwnerId()));
        assertThat(bookingReturn.getFirst().getStatus(), equalTo(BookingStatus.REJECTED));
    }

}
