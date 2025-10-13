package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Transactional
public class ItemStorageTest {
    @Autowired
    private ItemStorage itemStorage;

    @Autowired
    private TestEntityManager testEntityManager;

    private User user1;
    private User user2;
    private ItemRequest itemRequest;
    private Item item1;
    private Item item2;

    @BeforeEach
    public void addItems() {

        user1 = testEntityManager.persist(new User(null, "Name", "email@email.ru"));
        user2 = testEntityManager.persist(new User(null, "Name2", "email2@email.ru"));
        itemRequest = testEntityManager.persist(new ItemRequest(null, "description", user2,
                LocalDateTime.of(2025, 9, 9, 12, 0)));
        testEntityManager.clear();
        item1 = itemStorage.save(new Item(null, "Name", "description", true, user2.getId(), null));
        item2 = itemStorage.save(new Item(null, "Test2", "description2", true, user1.getId(), itemRequest.getId()));
    }

    @Test
    void findByOwnerId() {
        List<Item> result = itemStorage.findByOwnerId(user2.getId());
        assertEquals(List.of(item1), result);
    }

    @Test
    void findByRequestIdIn() {
        List<Item> result = itemStorage.findByRequestIdIn(Set.of(itemRequest.getId()));
        assertEquals(List.of(item2), result);
    }

    @Test
    void searchByText_findByName() {
        List<Item> result = itemStorage.searchByText("est");
        assertEquals(List.of(item2), result);
    }

    @Test
    void searchByText_findByDescription() {
        List<Item> result = itemStorage.searchByText("tion");
        assertEquals(List.of(item1, item2), result);
    }

/*
    @AfterEach
    public void clear() {
        testEntityManager.clear();
        itemRequestStorage.deleteAll();
        userStorage.deleteAll();
    }

 */
}
