package ru.practicum.shareit.item;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
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
public class ItemServiceTest {

    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;
    private final EntityManager entityManager;
    private UserCreateDto userCreateDto;
    private ItemCreateDto itemCreateDto;



    @BeforeEach
     void init() {
        userCreateDto = UserCreateDto.builder().name("Люся").email("lusya@mail.ru").build();
        itemCreateDto = ItemCreateDto.builder().name("фен").description("проф.").isAvailable(true).build();
    }

    @Test
    void addComment() {
        UserDto userDto = userService.create(userCreateDto);
        itemCreateDto.setOwnerId(userDto.getId());
        ItemDto itemDto = itemService.addItem(itemCreateDto);
        UserCreateDto anotherUser = UserDto.builder().name("Марсик").email("marsik@mail.ru").build();
        UserDto booker = userService.create(anotherUser);
        BookingCreateDto bookingCreateDto = BookingCreateDto.builder().itemId(itemDto.getId()).bookerId(booker.getId())
                .start(LocalDateTime.now().plusMinutes(1L)).end(LocalDateTime.now().plusMinutes(2L)).build();
        bookingService.addBooking(bookingCreateDto);
        CreateCommentDto createCommentDto = CreateCommentDto.builder().itemId(itemDto.getId()).authorId(booker.getId())
                .text("хороший фен").build();
        CommentDto commentDto = itemService.addComment(createCommentDto);
        TypedQuery<Comment> typedQuery = entityManager.createQuery("select c from Comment c where c.id = :commentDtoId",
                Comment.class);
        Comment comment = typedQuery.setParameter("commentDtoId", commentDto.getId()).getSingleResult();

        assertThat(commentDto.getId(), notNullValue());
        assertThat(comment.getId(), equalTo(commentDto.getId()));
        assertThat(comment.getAuthor(), equalTo(userMapper.toEntity(booker)));
        assertThat(comment.getItem().getName(), equalTo(itemMapper.toEntity(itemDto).getName()));
        assertThat(comment.getText(), equalTo(commentDto.getText()));
        assertThat(comment.getCreated(), notNullValue());
    }

    @Test
    void allItems() {
        UserDto userDto = userService.create(userCreateDto);
        itemCreateDto.setOwnerId(userDto.getId());
        ItemDto addOne = itemService.addItem(itemCreateDto);
        ItemCreateDto two= ItemCreateDto.builder().name("кран").description("смеситель").isAvailable(true)
                .ownerId(userDto.getId()).build();
        ItemDto addTwo = itemService.addItem(two);

        List<ItemDtoAllFields> allFields = itemService.allItems(userDto.getId(), 1, 10);
        assertThat(allFields.size(), equalTo(2));
        assertThat(allFields.get(0).getOwnerId(), equalTo(userDto.getId()));
        assertThat(allFields.get(1).getOwnerId(), equalTo(userDto.getId()));
        assertThat(allFields.get(1).getId(), equalTo(addOne.getId()));
        assertThat(allFields.get(0).getId(), equalTo(addTwo.getId()));

    }

    @Test
    void getItem() {
        UserDto userDto = userService.create(userCreateDto);
        itemCreateDto.setOwnerId(userDto.getId());
        ItemDto itemDto = itemService.addItem(itemCreateDto);

        ItemDtoAllFields allFields = itemService.getItem(userDto.getId(), itemDto.getId());
        TypedQuery<Item> query = entityManager.createQuery("Select i from Item i where i.id = :itemDtoId", Item.class);
        Item item = query.setParameter("itemDtoId", itemDto.getId()).getSingleResult();

        assertThat(allFields.getComments(), notNullValue());
        assertThat(item.getName(), equalTo(allFields.getName()));
        assertThat(item.getDescription(), equalTo(allFields.getDescription()));

    }

    @Test
    void getItemsForRent() {
        UserDto userDto = userService.create(userCreateDto);
        itemCreateDto.setOwnerId(userDto.getId());
        ItemDto addOne = itemService.addItem(itemCreateDto);
        ItemCreateDto one= ItemCreateDto.builder().name("большой кран").description("смеситель").isAvailable(true)
                .ownerId(userDto.getId()).build();
        ItemDto addTwo = itemService.addItem(one);

        List<ResponseItem> responseItems = itemService.getItemsForRent(userDto.getId(),"большой кран");

        assertThat(responseItems.size(), equalTo(1));
        assertThat(responseItems.getFirst().getDescription(), equalTo(addTwo.getDescription()));
        assertThat(responseItems.getFirst().getName(), equalTo(addTwo.getName()));

    }
    @Test
    void addItem() {
        UserDto userDto = userService.create(userCreateDto);
        itemCreateDto.setOwnerId(userDto.getId());
        ItemDto itemDto = itemService.addItem(itemCreateDto);

        ItemDtoAllFields allFields = itemService.getItem(userDto.getId(), itemDto.getId());
        TypedQuery<Item> query = entityManager.createQuery("Select i from Item i where i.id = :itemDtoId", Item.class);
        Item item = query.setParameter("itemDtoId", itemDto.getId()).getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getId(), equalTo(allFields.getId()));
        assertThat(item.getName(), equalTo(allFields.getName()));
        assertThat(item.getDescription(), equalTo(allFields.getDescription()));

    }

    @Test
    void updateItem() {
        UserDto userDto = userService.create(userCreateDto);
        itemCreateDto.setOwnerId(userDto.getId());
        ItemDto itemDto = itemService.addItem(itemCreateDto);
        RequestItem requestItem = RequestItem.builder().id(itemDto.getId()).name("новый фен").isAvailable(true)
                .description("новый").build();

        ItemDto responseItem = itemService.updateItem(userDto.getId(), requestItem);
        TypedQuery<Item> query = entityManager.createQuery("Select i from Item i where i.id = :itemDtoId", Item.class);
        Item item = query.setParameter("itemDtoId", itemDto.getId()).getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getId(), equalTo(responseItem.getId()));
        assertThat(item.getName(), equalTo(responseItem.getName()));
        assertThat(item.getDescription(), equalTo(responseItem.getDescription()));
    }

}
