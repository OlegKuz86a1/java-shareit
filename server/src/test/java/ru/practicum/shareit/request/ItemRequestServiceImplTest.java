package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreate;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Spy
    private RequestMapper mapper;

    @Spy
    private ItemMapper itemMapper;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    private static ItemRequestCreate itemRequestCreate;
    private static User user;
    private static ItemRequest itemRequest;
    private static Item item;
    private final long USERID = 7L;
    private final long ITEMREQUESTID = 8L;

    @BeforeEach
    void setUp() {
        user = User.builder().id(USERID).name("Люся").email("Lusya@mail.ru").build();
        itemRequestCreate = ItemRequestCreate.builder().description("Нужен фотоаппарат").requestor(6L).build();
        itemRequest = ItemRequest.builder().id(ITEMREQUESTID).requestor(user).description("Нужен фотоаппарат")
                .dateRequestCreated(LocalDateTime.now()).build();
        item = Item.builder().name("фотоаппарат").description("Nikon, профессиональный").owner(user)
                .itemRequest(itemRequest).isAvailable(true).build();

    }

    @Test
    void createNewItemRequestThenCallSaveToRepositoryAndReturnDtoWith() {
        when(userRepository.findById(USERID)).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(Mockito.any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDto itemRequestDto = itemRequestService.create(USERID, itemRequestCreate);

        assertThat(itemRequestDto, equalTo(mapper.toDto(itemRequest)));
        Mockito.verify(userRepository, Mockito.times(1)).findById(USERID);
        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .save(Mockito.any(ItemRequest.class));

    }

    @Test
    void createNewItemRequestByNotExistsUserThenReturnNotFoundException() {
        when(userRepository.findById(USERID)).thenThrow(new NotFoundException("User with id=7 not found"));

        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> itemRequestService.create(USERID, itemRequestCreate));

        Assertions.assertEquals("User with id=7 not found", exception.getMessage());

        Mockito.verify(userRepository, Mockito.times(1)).findById(USERID);
        Mockito.verify(itemRequestRepository, Mockito.never()).save(Mockito.any(ItemRequest.class));
    }

    @Test
    void getRequestsByUserIdAndThenReturnItemRequestDtoWithItems() {
        when(mapper.mapToRequestWithItem(any(ItemRequest.class), any(List.class)))
                .thenReturn(mock(ItemRequestDtoWithItems.class));
        when(userRepository.existsById(USERID)).thenReturn(true);
        when(itemRequestRepository.findAllByRequestorIdOrderByDateRequestCreatedDesc(anyLong()))
                .thenReturn(List.of(itemRequest));
        when(itemRepository.findItemsByItemRequestId(anyLong())).thenReturn(List.of(item));

        List<ItemRequestDtoWithItems> itemsList = itemRequestService.getRequestsByUserId(USERID);

        assertNotNull(itemsList.getFirst().getItems());
        verify(userRepository, times(1)).existsById(USERID);
        verify(itemRequestRepository, times(1))
                .findAllByRequestorIdOrderByDateRequestCreatedDesc(anyLong());
        verify(itemRepository, times(1)).findItemsByItemRequestId(anyLong());
    }

    @Test
    void getAllRequestsThenReturnItemRequestDtoWithItems() {
        when(mapper.mapToRequestWithItem(any(ItemRequest.class), any(List.class)))
                .thenAnswer(invocation -> mock(ItemRequestDtoWithItems.class));
        when(itemRequestRepository.findOtherUsersRequests(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(mock(ItemRequest.class))));
        when(itemRepository.findItemsByItemRequestId(anyLong())).thenReturn(List.of(item));

        List<ItemRequestDtoWithItems> dtoWithItems = itemRequestService.getAllRequests(USERID, 1, 10);

        assertNotNull(dtoWithItems.getFirst().getItems());
        verify(itemRequestRepository, Mockito.times(1))
                .findOtherUsersRequests(Mockito.anyLong(), Mockito.any(Pageable.class));
        verify(itemRepository, Mockito.times(1)).findItemsByItemRequestId(Mockito.anyLong());
    }

    @Test
    void getThreeRequestsByUserIdAndThenCallThriceItemRepository() {
        ItemRequest itemRequest1 = ItemRequest.builder().id(1L).build();
        ItemRequest itemRequest2 = ItemRequest.builder().id(2L).build();
        ItemRequest itemRequest3 = ItemRequest.builder().id(3L).build();
        when(itemRepository.findItemsByItemRequestId(anyLong())).thenReturn(List.of(item));
        when(itemRequestRepository.findAllByRequestorIdOrderByDateRequestCreatedDesc(anyLong()))
                .thenReturn(List.of(itemRequest1, itemRequest2, itemRequest3));
        when(userRepository.existsById(USERID)).thenReturn(true);

        itemRequestService.getRequestsByUserId(USERID);
        verify(userRepository, times(1)).existsById(USERID);
        verify(itemRequestRepository, times(1))
                .findAllByRequestorIdOrderByDateRequestCreatedDesc(anyLong());
        verify(itemRepository, times(3)).findItemsByItemRequestId(anyLong());
    }

    @Test
    void getByIdByNotExistsItemRequestAndThenNotFoundException() {
        when(userRepository.existsById(USERID)).thenReturn(true);
        when(itemRequestRepository.findById(ITEMREQUESTID)).thenThrow(new NotFoundException("Item request with id=8 not found"));

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequestById(USERID, ITEMREQUESTID));
        assertEquals("Item request with id=8 not found", exception.getMessage());
        verify(itemRepository, never()).findItemsByItemRequestId(anyLong());
        verify(itemRequestRepository, times(1)).findById(anyLong());
        verify(userRepository,times(1)).existsById(USERID);

    }

    @Test
    void getItemRequestByIdThenReturnItemRequestDtoWithItems() {
        when(mapper.mapToRequestWithItem(any(ItemRequest.class), any(List.class)))
                .thenReturn(mock(ItemRequestDtoWithItems.class));

        when(itemRepository.findItemsByItemRequestId(ITEMREQUESTID)).thenReturn(List.of(item));
        when(itemRequestRepository.findById(ITEMREQUESTID)).thenReturn(Optional.of(itemRequest));
        when(userRepository.existsById(USERID)).thenReturn(true);
        ItemRequestDtoWithItems dtoWithItems = itemRequestService.getRequestById(USERID, ITEMREQUESTID);
        assertNotNull(dtoWithItems.getItems());

        verify(userRepository, Mockito.times(1)).existsById(USERID);
        verify(itemRequestRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findItemsByItemRequestId(anyLong());
    }

    @Test
    void getByIdByNotExistsUserAndThenNotFoundException() {
        when(userRepository.existsById(USERID)).thenReturn(false);
        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequestById(USERID, ITEMREQUESTID));

        assertEquals("User with id=7 not found", exception.getMessage());
        verify(itemRepository, never()).findItemsByItemRequestId(anyLong());
        verify(itemRequestRepository, never()).findById(anyLong());
        verify(userRepository, times(1)).existsById(USERID);

    }
}
