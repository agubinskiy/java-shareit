package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRequestStorageTest {
    @Autowired
    private ItemRequestStorage itemRequestStorage;

    @Autowired
    private UserStorage userStorage;

    private final LocalDateTime baseTime = LocalDateTime.of(2025, 9, 9, 12, 0);
    private User user;
    private ItemRequest itemRequest;

    @BeforeEach
    public void addItemRequest() {
        user = userStorage.save(new User(null, "Name", "email@email.ru"));
        itemRequest = itemRequestStorage.save(new ItemRequest(null, "description", user,
                baseTime));
    }

    @Test
    void findByRequesterId() {
        List<ItemRequest> result = itemRequestStorage.findByRequesterId(user.getId());
        assertEquals(List.of(itemRequest), result);
    }

    @BeforeEach
    public void clear() {
        itemRequestStorage.deleteAll();
        userStorage.deleteAll();
    }
}
