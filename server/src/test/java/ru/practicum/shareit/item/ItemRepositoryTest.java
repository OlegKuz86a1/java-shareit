package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ItemRepository itemRepository;


    @Test
    void findById() {
        Item oneItem = Item.builder().name("Фен").description("новый").isAvailable(true).build();
        Item twoItem = Item.builder().name("Лом").description("стальной").isAvailable(true).build();
        entityManager.persist(oneItem);
        entityManager.persist(twoItem);

        Optional<Item> returnItem = itemRepository.findById(oneItem.getId());
        assertNotNull(returnItem);
        assertThat(returnItem.get().getName(), equalTo(oneItem.getName()));
        assertThat(returnItem.get().getDescription(), equalTo(oneItem.getDescription()));
    }

    @Test
    void findItemsByOwnerId() {
        User oneUser = User.builder().name("Люся").email("lusya@mail.ru").build();
        Item oneItem = Item.builder().name("Фен").description("новый").owner(oneUser).isAvailable(true).build();
        Item twoItem = Item.builder().name("Лом").description("стальной").owner(oneUser).isAvailable(true).build();
        entityManager.persist(oneUser);
        entityManager.persist(oneItem);
        entityManager.persist(twoItem);

        Page<Item> returnItems = itemRepository.findItemsByOwnerId(oneUser.getId(), Pageable.ofSize(10));
        assertEquals(2, returnItems.getContent().size());
        assertThat(returnItems.getContent().get(0), equalTo(oneItem));
        assertThat(returnItems.getContent().get(1), equalTo(twoItem));


    }

    @Test
    void findItemsByItemRequestId() {
        LocalDateTime now = LocalDateTime.now();
        User oneUser = User.builder().name("Люся").email("lusya@mail.ru").build();

        ItemRequest request = ItemRequest.builder().description("нужно").requestor(oneUser).dateRequestCreated(now).build();
        Item oneItem = Item.builder().name("Фен").description("новый").owner(oneUser).isAvailable(true)
                .itemRequest(request).build();
        Item twoItem = Item.builder().name("Лом").description("стальной").owner(oneUser).isAvailable(true)
                .itemRequest(request).build();
        entityManager.persist(oneUser);
        entityManager.persist(oneItem);
        entityManager.persist(twoItem);
        entityManager.persist(request);

        List<Item> returnItems = itemRepository.findItemsByItemRequestId(request.getId());
        assertEquals(2, returnItems.size());
        assertThat(returnItems.get(0), equalTo(oneItem));
        assertThat(returnItems.get(1), equalTo(twoItem));
    }

}
