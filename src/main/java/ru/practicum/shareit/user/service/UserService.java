package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {
    UserDto addUser(User user);

    UserDto updateUser(long id, User user);

    UserDto getUserById(long id);

    void deleteUser(long id);
}
