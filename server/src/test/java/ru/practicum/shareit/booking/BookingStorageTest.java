package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class BookingStorageTest {
    @Autowired
    private ItemStorage itemStorage;

    @Autowired
    private UserStorage userStorage;

    @Autowired
    private BookingStorage bookingStorage;

    private User user1;
    private User user2;
    private Item item1;
    private Item item2;
    private Booking booking1;
    private Booking booking2;
    private Booking booking3;

    @BeforeEach
    public void addBookings() {
        bookingStorage.deleteAll();
        itemStorage.deleteAll();
        userStorage.deleteAll();
        user1 = userStorage.save(new User(null, "Name", "email@email.ru"));
        user2 = userStorage.save(new User(null, "Name2", "email2@email.ru"));
        item1 = itemStorage.save(new Item(null, "Name", "description", true, user1.getId(), null));
        item2 = itemStorage.save(new Item(null, "Test2", "description2", true, user2.getId(), null));
        booking1 = bookingStorage.save(new Booking(null,
                LocalDateTime.now().plusMinutes(10),
                LocalDateTime.now().plusMinutes(40), item1, user2, BookingStatus.WAITING));
        booking2 = bookingStorage.save(new Booking(null,
                LocalDateTime.now().minusMinutes(40),
                LocalDateTime.now().minusMinutes(10), item1, user1, BookingStatus.APPROVED));
        booking3 = bookingStorage.save(new Booking(null,
                LocalDateTime.now().minusMinutes(10),
                LocalDateTime.now().plusMinutes(10), item2, user2, BookingStatus.REJECTED));
    }

    @Test
    void findByBookerId() {
        List<Booking> result = bookingStorage.findByBookerId(user2.getId());
        assertEquals(List.of(booking1, booking3), result);
    }

    @Test
    void findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc() {
        List<Booking> result = bookingStorage.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(user2.getId(),
                LocalDateTime.now(), LocalDateTime.now());
        assertEquals(List.of(booking3), result);
    }

    @Test
    void findByBookerIdAndStartAfterOrderByStartDesc() {
        List<Booking> result = bookingStorage.findByBookerIdAndStartAfterOrderByStartDesc(user2.getId(),
                LocalDateTime.now());
        assertEquals(List.of(booking1), result);
    }

    @Test
    void findByBookerIdAndEndBeforeOrderByStartDesc() {
        List<Booking> result = bookingStorage.findByBookerIdAndEndBeforeOrderByStartDesc(user1.getId(),
                LocalDateTime.now());
        assertEquals(List.of(booking2), result);
    }

    @Test
    void findByBookerIdAndStatusOrderByStartDesc() {
        List<Booking> result = bookingStorage.findByBookerIdAndStatusOrderByStartDesc(user2.getId(),
                BookingStatus.WAITING);
        assertEquals(List.of(booking1), result);
    }

    @Test
    void findByBookerIdAndItemIdAndStatusAndEndBeforeOrderByStartDesc() {
        List<Booking> result = bookingStorage.findByBookerIdAndItemIdAndStatusAndEndBeforeOrderByStartDesc(user1.getId(),
                item1.getId(), BookingStatus.APPROVED, LocalDateTime.now());
        assertEquals(List.of(booking2), result);
    }

    @Test
    void findAllByOwnerId() {
        List<Booking> result = bookingStorage.findAllByOwnerId(user1.getId());
        assertEquals(List.of(booking1, booking2), result);
    }

    @Test
    void findCurrentByOwnerId() {
        List<Booking> result = bookingStorage.findCurrentByOwnerId(user2.getId(), LocalDateTime.now());
        assertEquals(List.of(booking3), result);
    }

    @Test
    void findFutureByOwnerId() {
        List<Booking> result = bookingStorage.findFutureByOwnerId(user1.getId(), LocalDateTime.now());
        assertEquals(List.of(booking1), result);
    }

    @Test
    void findPastByOwnerId() {
        List<Booking> result = bookingStorage.findPastByOwnerId(user1.getId(), LocalDateTime.now());
        assertEquals(List.of(booking2), result);
    }

    @Test
    void findByOwnerIdAndStatusOrderByStartDesc() {
        List<Booking> result = bookingStorage.findByOwnerIdAndStatusOrderByStartDesc(user1.getId(), BookingStatus.APPROVED);
        assertEquals(List.of(booking2), result);
    }
}
