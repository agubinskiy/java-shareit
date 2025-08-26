package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.errors.exceptions.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.storage.UserStorage;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public UserDto addUser(User user) {
        log.debug("Начинается добавление пользователя {}", user);
        userStorage.addUser(user);
        return UserMapper.toUserDto(user);
    }

    public UserDto updateUser(long id, User user) {
        log.debug("Начинается обновление пользователя Id={}, {}", id, user);
        return UserMapper.toUserDto(userStorage.updateUser(id, user));
    }

    public UserDto getUserById(long id) {
        log.debug("Выполняется запрос пользователя с id={}", id);
        return userStorage.getUserById(id)
                .map(UserMapper::toUserDto)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }

    public void deleteUser(long id) {
        log.debug("Удаляется пользователь с id={}", id);
        userStorage.deleteUser(id);
    }
}
