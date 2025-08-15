package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.User;

import java.util.Optional;

public interface UserStorage {
    User addUser(User user);

    User updateUser(long id, User user);

    Optional<User> getUserById(long id);

    void deleteUser(long id);
}
