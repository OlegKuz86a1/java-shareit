package ru.practicum.shareit.request;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestCreate;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(properties = "db.name=test", webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceTest {

    private final UserMapper userMapper;
    private final EntityManager entityManager;
    private final ItemService itemService;
    private final UserService userService;
    private final ItemRequestService requestService;
    private  UserCreateDto userCreateDto;
    private  ItemRequestCreate itemRequestCreate;

    @BeforeEach
     void setUp() {
        userCreateDto = UserCreateDto.builder().name("Люся").email("Lusya@mail.ru").build();
        itemRequestCreate = ItemRequestCreate.builder().description("Нужен фотоаппарат").build();
    }

    @Test
    void create() {
        UserDto userDto = userService.create(userCreateDto);
        ItemRequestDto itemRequestDto = requestService.create(userDto.getId(), itemRequestCreate);
        TypedQuery<ItemRequest> typedQuery = entityManager
                .createQuery("select i from ItemRequest i where i.id = :itemRequestId", ItemRequest.class);
        ItemRequest itemRequest = typedQuery.setParameter("itemRequestId", itemRequestDto.getId()).getSingleResult();
        assertThat(itemRequestDto.getId(), notNullValue());
        assertThat(itemRequestDto.getDateRequestCreated(), notNullValue());
        assertThat(itemRequestDto.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(itemRequestDto.getRequestor(), equalTo(userDto.getId()));
    }

    @Test
    void getRequestById() {
        UserDto userDto = userService.create(userCreateDto);
        ItemRequestDto gettingItemRequestDto = requestService.create(userDto.getId(), itemRequestCreate);
        TypedQuery<ItemRequest> typedQuery = entityManager
                .createQuery("select i from ItemRequest i where i.id = :itemRequestId", ItemRequest.class);
        ItemRequest itemRequest = typedQuery.setParameter("itemRequestId", gettingItemRequestDto.getId()).getSingleResult();
        assertThat(gettingItemRequestDto.getId(), equalTo(itemRequest.getId()));
        assertThat(gettingItemRequestDto.getDateRequestCreated(), equalTo(itemRequest.getDateRequestCreated()));
        assertThat(gettingItemRequestDto.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(gettingItemRequestDto.getRequestor(), equalTo(itemRequest.getRequestor().getId()));

    }

    @Test
    void getRequestsByUserId() {
        UserDto userDto = userService.create(userCreateDto);
        ItemRequestDto itemRequestDto = requestService.create(userDto.getId(), itemRequestCreate);
        ItemRequestCreate anotherOneItemRequestCreate = ItemRequestCreate.builder().description("нужен кот").build();
        ItemRequestDto anotherOneItemRequestDto = requestService.create(userDto.getId(), anotherOneItemRequestCreate);

        UserDto secondUserDto = userService.create(UserDto.builder().name("Марсик").email("marsik@mail.ru").build());
        ItemRequestCreate secondItemRequestCreate = ItemRequestCreate.builder().description("нужен второй кот").build();
        requestService.create(secondUserDto.getId(), secondItemRequestCreate);
        requestService.getRequestsByUserId(userDto.getId());

        TypedQuery<ItemRequest> typedQuery = entityManager
                .createQuery("select i from ItemRequest i where i.requestor.id = :requestorId", ItemRequest.class);
        List<ItemRequest> itemRequest = typedQuery.setParameter("requestorId", userDto.getId()).getResultList();

        assertThat(itemRequest.size(), equalTo(2));
        assertThat(itemRequest.getFirst().getId(), equalTo(itemRequest.getFirst().getId()));
        assertThat(itemRequest.get(0).getRequestor(), equalTo(userMapper.toEntity(userDto)));
        assertThat(itemRequest.get(0).getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(itemRequest.get(1).getId(), equalTo(itemRequest.get(1).getId()));
        assertThat(itemRequest.get(1).getRequestor(), equalTo(userMapper.toEntity(userDto)));
        assertThat(itemRequest.get(1).getDescription(), equalTo(anotherOneItemRequestDto.getDescription()));
    }
}
