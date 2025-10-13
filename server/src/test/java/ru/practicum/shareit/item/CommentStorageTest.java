package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentStorage;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class CommentStorageTest {
    @Autowired
    private ItemStorage itemStorage;

    @Autowired
    private UserStorage userStorage;

    @Autowired
    private CommentStorage commentStorage;

    private User user1;
    private Item item1;
    private Item item2;
    private Comment comment1;
    private Comment comment2;

    @BeforeEach
    public void addComments() {
        commentStorage.deleteAll();
        itemStorage.deleteAll();
        userStorage.deleteAll();
        user1 = userStorage.save(new User(null, "Name", "email@email.ru"));
        item1 = itemStorage.save(new Item(null, "Name", "description", true, user1.getId(), null));
        item2 = itemStorage.save(new Item(null, "Test2", "description2", true, user1.getId(), null));
        comment1 = commentStorage.save(new Comment(null, "text1", item2, user1, LocalDateTime.now()));
        comment2 = commentStorage.save(new Comment(null, "text2", item1, user1, LocalDateTime.now()));
    }

    @Test
    void findByItemIdIn() {
        List<Comment> result = commentStorage.findByItemIdIn(Set.of(item1.getId()));
        assertEquals(List.of(comment2), result);
    }
}
