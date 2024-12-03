package ru.practicum.shareit.item;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;

import ru.practicum.shareit.booking.dto.BookingDateDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.common.DateGenerator;
import ru.practicum.shareit.common.exception.IllegalDataException;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentMapperImpl;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Spy
    private ItemMapperImpl itemMapper;

    @Spy
    private CommentMapperImpl commentMapper;

    @Spy
    private BookingMapper bookingMapper;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private DateGenerator dateGenerator;

    @Mock
    private ItemRequestRepository requestRepository;

    @Mock
    private EntityManager em;

    @InjectMocks
    private ItemServiceImpl itemService;

    private ItemCreateDto itemCreateDto;
    private Item item;
    private User user;
    private CreateCommentDto createCommentDto;
    private RequestItem requestItem;
    private Comment comment;

    @BeforeEach
    public void beforeAll() {
        itemCreateDto = ItemCreateDto.builder().name("фотоаппарат").description("профессиональный, Nikon")
                .isAvailable(true).ownerId(7L).build();
        user = User.builder().id(1L).name("Люся").email("Lusya@mail.ru").build();

        createCommentDto = CreateCommentDto.builder().text("хороший фотоаппарат").itemId(1L).authorId(7L).build();

        item = itemMapper.mapToItem(itemCreateDto);
        item.setId(1L);

        requestItem = RequestItem.builder().id(9L).name("фотоаппарат").description("профессиональный, Nikon")
                .isAvailable(true).build();
        comment = Comment.builder().id(2L).text("хороший фотоаппарат").item(item).author(user).build();
    }

    @Test
    void addCommentByUserNotExistsThenNotFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userRepository.findById(7L)).thenThrow(new NotFoundException("User not found with id: 7"));

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.addComment(createCommentDto));

        assertEquals("User not found with id: 7", exception.getMessage());
        verify(userRepository, times(1)).findById(7L);
        verify(itemRepository, times(1)).findById(1L);
        verify(bookingRepository, never()).existsByItemIdAndStatusAndEndAfter(
                        anyLong(),
                        eq(BookingStatus.APPROVED),
                        any(LocalDateTime.class));
        verify(commentRepository, never()).save(Mockito.any());
    }

    @Test
    void addCommentByItemNotExistsThenNotFound() {
        when(itemRepository.findById(1L)).thenThrow(new NotFoundException("Item not found with id: 1"));

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.addComment(createCommentDto));

        assertEquals("Item not found with id: 1", exception.getMessage());

        verify(itemRepository, times(1)).findById(1L);
        verify(userRepository, never()).findById(anyLong());
        verify(bookingRepository, never()).existsByItemIdAndStatusAndEndAfter(
                anyLong(),
                eq(BookingStatus.APPROVED),
                any(LocalDateTime.class));
        verify(commentRepository, never()).save(Mockito.any());
    }

    @Test
    void addCommentButBookingDoesNotExistsThenIllegalDataException() {
        LocalDateTime now = LocalDateTime.now();
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(dateGenerator.getCurrentTime()).thenReturn(now);
        when(bookingRepository.existsByItemIdAndStatusAndEndAfter(1L, BookingStatus.APPROVED, now)).thenReturn(true);

        final IllegalDataException exception = assertThrows(IllegalDataException.class,
                () -> itemService.addComment(createCommentDto));

        assertEquals("Item in approved booking", exception.getMessage());
        verify(itemRepository, times(1)).findById(1L);
        verify(bookingRepository, times(1)).existsByItemIdAndStatusAndEndAfter(
                1L, BookingStatus.APPROVED, now);
        verify(userRepository, never()).findById(anyLong());
        verify(commentRepository, never()).save(Mockito.any());
    }
    @Test
    void getItemsForRentThenCallSearchItemRepository() {
        List<Item> items = List.of(item);
        when(userRepository.findById(7L)).thenReturn(Optional.of(user));
        when(itemRepository.findItemsByText(anyString())).thenReturn(items);

        List<ResponseItem> responseItems = itemService.getItemsForRent(7L, "фен");
        assertThat(responseItems.size(), equalTo(1));
        verify(itemRepository, times(1)).findItemsByText(anyString());
        verify(userRepository, times(1)).findById(7L);
    }


    @Test
    void createItemByUserNotExistsThenNotFound() {
        when(userRepository.findById(7L)).thenThrow(new NotFoundException("User not found with id: 7"));

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.addItem(itemCreateDto));
        assertEquals("User not found with id: 7", exception.getMessage());
        verify(userRepository, times(1)).findById(7L);
    }

    @Test
    void createItemByExistsUserThenCallSaveItemRepository() {
        when(userRepository.findById(itemCreateDto.getOwnerId())).thenReturn(Optional.of(user));
        when(itemRepository.save(Mockito.any())).thenReturn(item);

        ItemDto returnedItem = itemService.addItem(itemCreateDto);
        assertThat(returnedItem, equalTo(itemMapper.toDto(item)));

        verify(userRepository).findById(itemCreateDto.getOwnerId());
        verify(itemRepository, Mockito.times(1)).save(Mockito.any());

    }

    @Test
    void updateItemByUserNotExistsThenNotFound() {
        when(userRepository.findById(7L)).thenThrow(new NotFoundException("User not found with id: 7"));

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.updateItem(7L, requestItem ));
        assertEquals("User not found with id: 7", exception.getMessage());
        verify(userRepository, times(1)).findById(7L);
        verify(itemRepository, Mockito.never()).findById(Mockito.anyLong());
        verify(itemRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void updateByItemNotExistsThenNotFound() {
        when(userRepository.findById(7L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(9L)).thenThrow(new NotFoundException("Item not found with id: 9"));

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.updateItem(7L, requestItem));

        assertEquals("Item not found with id: 9", exception.getMessage());

        verify(itemRepository, times(1)).findById(9L);
        verify(bookingRepository, never()).existsByItemIdAndStatusAndEndAfter(anyLong(), eq(BookingStatus.APPROVED),
                any(LocalDateTime.class));
        verify(commentRepository, never()).save(Mockito.any());
    }

    @Test
    void updateItemByUserNotOwnerThenNotFound() {
        User oneMoreUser = User.builder().id(6L).name("Marsik").email("marsik@mail.ru").build();
        Item itemWithOneMoreUser = Item.builder().id(10L).owner(oneMoreUser).description("стальной лом").name("лом")
                .isAvailable(true).build();
        when(userRepository.findById(7L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(9L)).thenReturn(Optional.of(itemWithOneMoreUser));
        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.updateItem(7L, requestItem));

        assertEquals("only the owner can change a item", exception.getMessage());
        verify(userRepository, times(1)).findById(7L);
        verify(itemRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void findCommentByIdThenReturnComment() {

        when(commentRepository.findById(2L)).thenReturn(Optional.of(comment));

        Comment returned = itemService.findById(2L);
        Mockito.verify(commentRepository, Mockito.times(1)).findById(2L);
        assertEquals(comment, returned);
    }

    @Test
    void getItemByIdUserNotOwnerAndThenReturnItemBookingsDateNull() {
        List<Comment> comments = List.of(comment);
        User owner = User.builder().id(8L).build();
        item.setOwner(owner);
        when(userRepository.findById(7L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(5L)).thenReturn(Optional.of(item));
        Mockito.when(commentRepository.findCommentsByItemId(5L)).thenReturn(comments);

        ItemDtoAllFields allFields = itemService.getItem(7L, 5L);
        assertNull(allFields.getNearestBooking());
        assertNull(allFields.getLastBooking());
        assertThat(allFields.getComments(), hasSize(1));

        CommentDto returnedComment = commentMapper.toDto(comments.getFirst());
        assertThat(allFields, equalTo(itemMapper.mapToItemDtoAllFields(item, null, null, List.of(returnedComment))));
        verify(itemRepository, Mockito.times(1)).findById(Mockito.anyLong());
        verify(commentRepository, Mockito.times(1)).findCommentsByItemId(Mockito.anyLong());

    }

    @Test
    void getAllItemsByIdOwnerAndThenReturnItemBookingsDateNull() {
        Booking nearest = Booking.builder().item(item).booker(user).start(LocalDateTime.now().plusDays(1)).build();
        Booking last = Booking.builder().item(item).booker(user).start(LocalDateTime.now().plusDays(1)).build();
        Page<Item> itemsPage = new PageImpl<>(List.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findItemsByOwnerId(anyLong(), any(Pageable.class))).thenReturn(itemsPage);
        when(bookingRepository.getByItemIdAndStartAfterOrderByStartAsc(anyLong(), any(LocalDateTime.class)))
                .thenReturn(nearest);
        when(bookingRepository.findByItemIdAndEndBeforeOrEqualsOrderByStartDesc(anyLong(), any(LocalDateTime.class)))
                .thenReturn(last);
        BookingDateDto nearestBooking = bookingMapper.toBookingDateDto(nearest);
        BookingDateDto lastBooking = bookingMapper.toBookingDateDto(last);

        List<ItemDtoAllFields> allFields = itemService.allItems(7L, 1, 10);
        assertThat(allFields.getFirst(), equalTo(itemMapper.mapToItemDtoAllFields(item, lastBooking, nearestBooking, null)));
        verify(itemRepository, times(1)).findItemsByOwnerId(7L,
                PageRequest.of(1 / 10, 10, Sort.Direction.DESC, "id"));
        verify(bookingRepository, times(1)).getByItemIdAndStartAfterOrderByStartAsc(anyLong(),
                any(LocalDateTime.class));
        verify(bookingRepository, times(1))
                .findByItemIdAndEndBeforeOrEqualsOrderByStartDesc(anyLong(), any(LocalDateTime.class));
    }

}
