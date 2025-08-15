package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.errors.exceptions.DuplicateDataException;
import ru.practicum.shareit.user.User;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
@Slf4j
public class UserInMemoryStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private long counter;

    public User addUser(User user) {
        emailCheckDuplicate(user.getEmail());
        user.setId(++counter);
        users.put(counter, user);
        emails.add(user.getEmail());
        log.info("Пользователь с id={} успешно добавлен", counter);
        log.debug("Добавлен пользователь {}", user);
        return user;
    }

    public User updateUser(long id, User user) {
        User oldUser = users.get(id);
        if (user.getName() != null) {
            oldUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            emailCheckDuplicate(user.getEmail());
            emails.remove(oldUser.getEmail());
            oldUser.setEmail(user.getEmail());
            emails.add(user.getEmail());
        }
        log.info("Пользователь успешно обновлён {}", oldUser);
        return oldUser;
    }

    public Optional<User> getUserById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    public void deleteUser(long id) {
        users.remove(id);
    }

    private void emailCheckDuplicate(String email) {
        if (emails.contains(email)) {
            throw new DuplicateDataException("Эта почта уже используется");
        }
    }
}
