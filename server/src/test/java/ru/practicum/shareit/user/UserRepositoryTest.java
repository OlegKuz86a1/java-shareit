package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void getUserById() {
       User user = User.builder().name("Люся").email("Lusya@mail.ru").build();
        entityManager.persist(user);
        entityManager.flush();

        Optional<User> foundUser = userRepository.findById(user.getId());
        assertTrue(foundUser.isPresent());
        assertThat(foundUser.get().getName(), equalTo(user.getName()));
        assertThat(foundUser.get().getEmail(),equalTo(user.getEmail()));
    }

    @Test
    void existsUserByEmail() {
        User user = User.builder().name("Люся").email("Lusya@mail.ru").build();

        entityManager.persist(user);

        Boolean doesExist = userRepository.existsUserByEmail("Lusya@mail.ru");
        assertThat(doesExist, equalTo(true));
    }
}
