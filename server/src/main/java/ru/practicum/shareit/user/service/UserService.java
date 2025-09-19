package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {
    UserDto addUser(NewUserRequest user);

    UserDto updateUser(long id, NewUserRequest user);

    UserDto getUserById(long id);

    void deleteUser(long id);
}
