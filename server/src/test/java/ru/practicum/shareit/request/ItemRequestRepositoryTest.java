package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
public class ItemRequestRepositoryTest {
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findAllByRequestorIdOrderByDateRequestCreatedDesc() {
        User requestorFirst = User.builder().name("Люся"). email("lusya@mail.ru").build();
        User requestorSecond = User.builder().name("Марсик"). email("marsik@mail.ru").build();
        ItemRequest firstRequest = ItemRequest.builder().requestor(requestorFirst).description("Нужен кот")
                .dateRequestCreated(LocalDateTime.now()).build();
        ItemRequest secondRequest = ItemRequest.builder().requestor(requestorFirst).description("Нужен еще 1 кот")
                .dateRequestCreated(LocalDateTime.now()).build();
        ItemRequest thirdRequest = ItemRequest.builder().requestor(requestorSecond).description("Кто забрал всех котов")
                .dateRequestCreated(LocalDateTime.now()).build();
        entityManager.persist(requestorFirst);
        entityManager.persist(requestorSecond);
        entityManager.persist(firstRequest);
        entityManager.persist(secondRequest);
        entityManager.persist(thirdRequest);

        List<ItemRequest> itemRequests = itemRequestRepository
                .findAllByRequestorIdOrderByDateRequestCreatedDesc(requestorFirst.getId());
        List<ItemRequest> itemRequests2 = itemRequestRepository
                .findAllByRequestorIdOrderByDateRequestCreatedDesc(requestorSecond.getId());
        Assertions.assertEquals(2, itemRequests.size());
        Assertions.assertEquals(1, itemRequests2.size());

        assertThat(itemRequests.getFirst(), equalTo(secondRequest));
        assertThat(itemRequests.get(1), equalTo(firstRequest));
        assertThat(itemRequests2.getFirst(), equalTo(thirdRequest));


    }

    @Test
    void findOtherUsersRequests() {
        User requestorFirst = User.builder().name("Люся").email("lusya@mail.ru").build();
        User requestorSecond = User.builder().name("Марсик").email("marsik@mail.ru").build();
        ItemRequest firstRequest = ItemRequest.builder().requestor(requestorFirst).description("Нужен кот")
                .dateRequestCreated(LocalDateTime.now()).build();
        ItemRequest secondRequest = ItemRequest.builder().requestor(requestorFirst).description("Нужен еще 1 кот")
                .dateRequestCreated(LocalDateTime.now()).build();
        ItemRequest thirdRequest = ItemRequest.builder().requestor(requestorSecond).description("Кто забрал всех котов")
                .dateRequestCreated(LocalDateTime.now()).build();
        entityManager.persist(requestorFirst);
        entityManager.persist(requestorSecond);
        entityManager.persist(firstRequest);
        entityManager.persist(secondRequest);
        entityManager.persist(thirdRequest);

        Page<ItemRequest> itemRequests = itemRequestRepository.findOtherUsersRequests(requestorFirst.getId(),
                Pageable.ofSize(5));

        Assertions.assertEquals(1, itemRequests.getContent().size());
        assertThat(itemRequests.getContent().getFirst(), equalTo(thirdRequest));
        Assertions.assertEquals(1, itemRequests.getTotalPages());


    }
}
