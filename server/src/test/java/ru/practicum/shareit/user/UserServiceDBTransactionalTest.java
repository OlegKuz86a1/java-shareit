package ru.practicum.shareit.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(properties = "db.name=test", webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceDBTransactionalTest {

    private final EntityManager entityManager;
    private final UserService userService;
    private static UserCreateDto userCreateDto;

    @BeforeAll
    static void setUser() {
         userCreateDto = UserCreateDto.builder().name("Люся").email("lusya@mail.ru").build();
    }

    @Test
    void create() {
        UserDto userDto = userService.create(userCreateDto);

        TypedQuery<User> managerQuery = entityManager.createQuery("select u from User u where u.email = :email", User.class);
        User user = managerQuery.setParameter("email", userDto.getEmail()).getSingleResult();

        assertThat(userDto.getId(), notNullValue());
        assertThat(user.getId(), equalTo(userDto.getId()));
        assertThat(user.getName(), equalTo(userCreateDto.getName()));
        assertThat(user.getEmail(), equalTo(userCreateDto.getEmail()));
    }

    @Test
    void update() {
        UserDto userDto = userService.create(userCreateDto);
        UserUpdateDto newUser = UserUpdateDto.builder().name("Марсик").email("marsik@mail.ru").build();
        userService.update(userDto.getId(),newUser);

        TypedQuery<User> managerQuery = entityManager.createQuery("select u from User u where u.id = :userId", User.class);
        User user = managerQuery.setParameter("userId", userDto.getId()).getSingleResult();

        assertThat(user.getName(), equalTo(newUser.getName()));
        assertThat(user.getEmail(), equalTo(newUser.getEmail()));

    }

    @Test
    void getById() {
        UserDto userDto = userService.create(userCreateDto);

        TypedQuery<User> managerQuery = entityManager.createQuery("select u from User u where u.id = :userId", User.class);
        User user = managerQuery.setParameter("userId", userDto.getId()).getSingleResult();

        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void delete() {
        UserDto userDto = userService.create(userCreateDto);
         assertThat(userDto.getId(), notNullValue());

         userService.delete(userDto.getId());

         User user = entityManager.find(User.class, userDto.getId());
         Assertions.assertNull(user);
    }
}
