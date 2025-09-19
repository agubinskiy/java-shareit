package ru.practicum.shareit.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.storage.UserStorage;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class UserStorageTest {
    @Autowired
    private UserStorage userStorage;

    @BeforeEach
    public void addUser() {
        User user = new User(null, "Name", "email@email.ru");
        userStorage.save(user);
    }

    @Test
    void existsByEmail() {
        assertTrue(userStorage.existsByEmail("email@email.ru"));
        assertFalse(userStorage.existsByEmail("email1@email.ru"));
    }

    @AfterEach
    public void deleteUsers() {
        userStorage.deleteAll();
    }
}
