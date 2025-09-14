package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.errors.exceptions.DuplicateDataException;
import ru.practicum.shareit.errors.exceptions.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.storage.UserStorage;

@Service
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    @Transactional
    public UserDto addUser(NewUserRequest request) {
        log.debug("Начинается добавление пользователя {}", request);
        emailCheckDuplicate(request);
        User user = UserMapper.mapFromNewUserRequest(request);
        userStorage.save(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUser(long id, NewUserRequest user) {
        log.debug("Начинается обновление пользователя Id={}, {}", id, user);
        User oldUser = userStorage.findById(id).orElseThrow(
                () -> new NotFoundException("Пользователь с id=" + id + " не найден")
        );
        emailCheckDuplicate(user);
        if (user.getName() != null) {
            oldUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            oldUser.setEmail(user.getEmail());
        }
        log.info("Пользователь успешно обновлён {}", oldUser);
        User updatedUser = userStorage.save(oldUser);
        return UserMapper.toUserDto(updatedUser);
    }


    @Override
    public UserDto getUserById(long id) {
        log.debug("Выполняется запрос пользователя с id={}", id);
        return userStorage.findById(id)
                .map(UserMapper::toUserDto)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }

    @Override
    public void deleteUser(long id) {
        log.debug("Удаляется пользователь с id={}", id);
        userStorage.deleteById(id);
    }

    private void emailCheckDuplicate(NewUserRequest user) {
        if (userStorage.existsByEmail(user.getEmail())) {
            throw new DuplicateDataException("Почта " + user.getEmail() + "уже используется");
        }
    }
}
