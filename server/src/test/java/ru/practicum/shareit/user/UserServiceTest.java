package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.practicum.shareit.common.exception.DuplicateException;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapperImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.Optional;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Spy
    protected UserMapperImpl userMapperImpl;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;
    private UserCreateDto userCreateDto;
    private User user;
    private UserUpdateDto userUpdateDto;

    @BeforeEach
    public void setUp() {
        userCreateDto = UserCreateDto.builder().name("Люся").email("Lusya@mail.ru").build();
        user = User.builder().id(1L).name("Люся").email("Lusya@mail.ru").build();
    }

    @Test
    void getUserByIdAndReturnedUser() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto actualUser = userService.getById(userId);

        assertThat(actualUser, equalTo(userMapperImpl.toDto(user)));
        verify(userRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    void getUserByNotExistUserAndThenThrowNotFoundException() {
        when(userRepository.findById(7L)).thenThrow(new NotFoundException("User with id=7 not found"));
        final NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.getById(7L));

        assertEquals("User with id=7 not found", exception.getMessage());
        verify(userRepository, Mockito.times(1)).findById(7L);
    }

    @Test
    void deleteUserByIdAndCallDeleteRepository() {
        userService.delete(1L);
        verify(userRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    void createUserWithCorrectData() {
        when(userRepository.save(Mockito.any())).thenReturn(user);

        UserDto returned = userService.create(userCreateDto);
        System.out.println("RETURNED = " + returned);

        assertThat(returned, equalTo(userMapperImpl.toDto(user)));
        verify(userRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void updateNotExistsUserThenThrowNotFoundException() {
        when(userRepository.findById(2L)).thenThrow(new NotFoundException("User with id=2 not found"));
        final NotFoundException exception = assertThrows(NotFoundException.class,
                ()->userService.update(2L,userUpdateDto));

        assertEquals("User with id=2 not found", exception.getMessage());
    }

    @Test
    void updateUserWithExistsEmailThenThrowDuplicateException() {
        UserUpdateDto updateDto = UserUpdateDto.builder().name("Женя").email("zhaba@ya.ru").build();
        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        when(userRepository.existsUserByEmail("zhaba@ya.ru"))
                .thenThrow(new DuplicateException("User with email=zhaba@ya.ru already exists"));

        final DuplicateException exception = assertThrows(DuplicateException.class,
                () -> userService.update(5L, updateDto));

        assertEquals("User with email=zhaba@ya.ru already exists", exception.getMessage());
    }

    @Test
    void updateUserWithEmailThenCallMethodsRepository() {

        UserUpdateDto userForUpdate = UserUpdateDto.builder().name("Марсик").email("Marsik@mail.ru").build();

        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        when(userRepository.existsUserByEmail(Mockito.anyString())).thenReturn(false);
        when(userRepository.save(Mockito.any())).thenReturn(user);

        UserDto userUpdated = userService.update(7L, userForUpdate);

        assertThat(userUpdated, equalTo(userMapperImpl.toDto(user)));
        assertThat(userUpdated.getId(), equalTo(user.getId()));
        assertThat(userUpdated.getEmail(),equalTo(user.getEmail()));
        assertThat(userUpdated.getName(), equalTo(user.getName()));

        verify(userRepository, Mockito.times(1)).findById(Mockito.anyLong());
        verify(userRepository, Mockito.times(1)).existsUserByEmail(Mockito.anyString());
        verify(userRepository, Mockito.times(1)).save(user);

    }
    @Test
    void updateUserWithoutEmailThenNotCallExistsByEmailRepository() {
        UserUpdateDto userWithoutEmail = UserUpdateDto.builder().name("Марсик").build();

        when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(Mockito.any())).thenReturn(user);

        UserDto userUpdated = userService.update(1L, userWithoutEmail);

        assertNotNull(userUpdated);
        assertThat(userUpdated, equalTo(userMapperImpl.toDto(user)));
        assertThat(userUpdated.getEmail(), equalTo("Lusya@mail.ru"));
        assertThat(userUpdated.getName(), equalTo("Марсик"));

        verify(userRepository, Mockito.times(1)).findById(Mockito.anyLong());
        verify(userRepository, Mockito.times(1)).save(user);
        verify(userRepository, Mockito.never()).existsUserByEmail(Mockito.anyString());
    }

}
