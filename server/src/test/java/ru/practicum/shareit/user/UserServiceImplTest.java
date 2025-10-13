package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.errors.exceptions.DuplicateDataException;
import ru.practicum.shareit.errors.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserStorage userStorage;

    @InjectMocks
    private UserServiceImpl userService;

    private final UserDto userDto = new UserDto(1L, "Name", "email@email.ru");

    @Test
    void addUser() {
        User userToReturn = new User(1L, "Name", "email@email.ru");
        NewUserRequest request = new NewUserRequest();
        request.setName("Name");
        request.setEmail("email@email.ru");

        when(userStorage.save(any(User.class))).thenReturn(userToReturn);

        UserDto actualUser = userService.addUser(request);

        assertEquals(userDto, actualUser);
        verify(userStorage).save(any(User.class));
    }

    @Test
    void addUser_DuplicateEmail() {
        NewUserRequest request = new NewUserRequest();
        request.setName("Name");
        request.setEmail("email@email.ru");

        when(userStorage.existsByEmail(any())).thenReturn(true);

        assertThrows(DuplicateDataException.class, () -> userService.addUser(request));
    }

    @Test
    void updateUser() {
        long userId = 1L;
        User userToReturn = new User(1L, "Name", "email@email.ru");
        NewUserRequest request = new NewUserRequest();
        request.setName("newName");
        request.setEmail("newemail@email.ru");

        when(userStorage.findById(userId)).thenReturn(Optional.of(userToReturn));
        when(userStorage.save(userToReturn)).thenReturn(userToReturn);

        UserDto updatedUser = userService.updateUser(userId, request);

        assertEquals("newName", updatedUser.getName());
        assertEquals("newemail@email.ru", updatedUser.getEmail());
        verify(userStorage).save(userToReturn);
    }

    @Test
    void updateUser_NotFound() {
        long userId = 1L;
        NewUserRequest request = new NewUserRequest();
        request.setName("newName");
        request.setEmail("newemail@email.ru");

        when(userStorage.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUser(userId, request));
    }

    @Test
    void getUserById() {
        long userId = 1L;
        User user = new User(1L, "Name", "email@email.ru");

        when(userStorage.findById(userId)).thenReturn(Optional.of(user));

        UserDto actualUser = userService.getUserById(userId);

        assertEquals(userDto, actualUser);
    }

    @Test
    void deleteUser() {
        long userId = 1L;

        userService.deleteUser(userId);

        verify(userStorage, times(1)).deleteById(userId);
    }
}
