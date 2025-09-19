package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.BookingStorage;
import ru.practicum.shareit.booking.dto.ItemBookingInfo;
import ru.practicum.shareit.errors.exceptions.ForbiddenException;
import ru.practicum.shareit.errors.exceptions.NotFoundException;
import ru.practicum.shareit.errors.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.storage.CommentStorage;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {
    @Mock
    private ItemStorage itemStorage;

    @Mock
    private UserStorage userStorage;

    @Mock
    private CommentStorage commentStorage;

    @Mock
    private BookingStorage bookingStorage;

    @InjectMocks
    private ItemServiceImpl itemService;

    private final LocalDateTime start = LocalDateTime.of(2025, 9, 9, 12, 0);
    private final LocalDateTime end = LocalDateTime.of(2025, 9, 9, 14, 0);
    private final User user = new User(1L, "Name", "email@email.ru");
    private final Item item = new Item(1L, "name", "description", true, 1L, 1L);
    private final Comment comment = new Comment(1L, "text", item, user, start);
    private final ItemWithBookingDto itemWithBookingDto = new ItemWithBookingDto(1L, "name",
            "description", true, 1L, 1L, start, end, List.of(comment));
    private final ItemBookingInfo bookingInfo = new ItemBookingInfo(1L, start, end);

    @Test
    void getAllItems() {
        long userId = 1L;

        when(itemStorage.findByOwnerId(userId)).thenReturn(List.of(item));
        when(commentStorage.findByItemIdIn(anySet())).thenReturn(List.of(comment));
        when(bookingStorage.findItemBookingInfo(anySet(), any(LocalDateTime.class))).thenReturn(List.of(bookingInfo));

        List<ItemWithBookingDto> result = itemService.getAllItems(userId);

        assertEquals(List.of(itemWithBookingDto), result);
    }

    @Test
    void getItemById() {
        long itemId = 1L;
        ItemWithBookingDto itemWithNoBookingDto = new ItemWithBookingDto(1L, "name",
                "description", true, 1L, 1L, null, null, List.of(comment));

        when(itemStorage.findById(itemId)).thenReturn(Optional.of(item));
        when(commentStorage.findByItemIdIn(anySet())).thenReturn(List.of(comment));

        ItemWithBookingDto result = itemService.getItemById(itemId);

        assertEquals(itemWithNoBookingDto, result);
    }

    @Test
    void getItemById_NotFound() {
        long itemId = 1L;

        when(itemStorage.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItemById(itemId));
    }

    @Test
    void searchByText() {
        String text = "text";
        List<ItemDto> expectedList = List.of(ItemMapper.toItemDto(item));

        when(itemStorage.searchByText(text)).thenReturn(List.of(item));

        List<ItemDto> result = itemService.searchByText(text);

        assertEquals(expectedList, result);
    }

    @Test
    void searchByBlankText() {
        String text = " ";

        List<ItemDto> result = itemService.searchByText(text);

        assertEquals(Collections.emptyList(), result);
    }

    @Test
    void addItem() {
        long userId = 1L;
        ItemDto itemDto = ItemMapper.toItemDto(item);
        NewItemRequest request = new NewItemRequest();
        request.setName("name");
        request.setDescription("description");
        request.setAvailable(true);
        request.setRequestId(1L);

        when(userStorage.findById(userId)).thenReturn(Optional.of(user));
        when(itemStorage.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.addItem(userId, request);

        assertEquals(itemDto, result);
        verify(itemStorage).save(any(Item.class));
    }

    @Test
    void addItem_UserNotFound() {
        long userId = 1L;
        NewItemRequest request = new NewItemRequest();
        request.setName("name");
        request.setDescription("description");
        request.setAvailable(true);
        request.setRequestId(1L);

        when(userStorage.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.addItem(userId, request));

        verify(itemStorage, never()).save(any(Item.class));
    }

    @Test
    void updateItem() {
        long userId = 1L;
        long itemId = 1L;
        NewItemRequest request = new NewItemRequest();
        request.setName("newName");
        request.setDescription("newDescription");
        request.setAvailable(true);
        request.setRequestId(null);

        when(itemStorage.save(any(Item.class))).thenReturn(item);
        when(itemStorage.findById(itemId)).thenReturn(Optional.of(item));

        ItemDto result = itemService.updateItem(userId, itemId, request);

        assertEquals("newName", result.getName());
        assertEquals("newDescription", result.getDescription());
        assertEquals(true, result.getAvailable());
        verify(itemStorage).save(any(Item.class));
    }

    @Test
    void updateItem_ItemNotFound() {
        long userId = 1L;
        long itemId = 1L;
        NewItemRequest request = new NewItemRequest();
        request.setName("newName");
        request.setDescription("newDescription");
        request.setAvailable(true);
        request.setRequestId(null);

        when(itemStorage.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.updateItem(userId, itemId, request));
        verify(itemStorage, never()).save(any(Item.class));
    }

    @Test
    void updateItem_UserNotOwner() {
        long userId = 2L;
        long itemId = 1L;
        NewItemRequest request = new NewItemRequest();
        request.setName("newName");
        request.setDescription("newDescription");
        request.setAvailable(true);
        request.setRequestId(null);

        when(itemStorage.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(ForbiddenException.class, () -> itemService.updateItem(userId, itemId, request));
        verify(itemStorage, never()).save(any(Item.class));
    }

    @Test
    void addComment() {
        long userId = 1L;
        long itemId = 1L;
        NewCommentRequest request = new NewCommentRequest();
        request.setText("text");
        Booking booking = new Booking(1L, LocalDateTime.of(2025, 9, 9, 12, 0),
                LocalDateTime.of(2025, 9, 9, 14, 0), item, user, BookingStatus.APPROVED);
        CommentDto commentDto = CommentMapper.mapToCommentDto(comment);

        when(userStorage.findById(userId)).thenReturn(Optional.of(user));
        when(itemStorage.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingStorage.findByBookerIdAndItemIdAndStatusAndEndBeforeOrderByStartDesc(eq(userId), eq(itemId),
                eq(BookingStatus.APPROVED),
                any(LocalDateTime.class)))
                .thenReturn(List.of(booking));
        when(commentStorage.save(any(Comment.class))).thenReturn(comment);

        CommentDto result = itemService.addComment(userId, itemId, request);

        assertEquals(commentDto, result);
        verify(commentStorage).save(any(Comment.class));
    }

    @Test
    void addComment_NotFoundUser() {
        long userId = 1L;
        long itemId = 1L;
        NewCommentRequest request = new NewCommentRequest();
        request.setText("text");
        Booking booking = new Booking(1L, LocalDateTime.of(2025, 9, 9, 12, 0),
                LocalDateTime.of(2025, 9, 9, 14, 0), item, user, BookingStatus.APPROVED);

        when(userStorage.findById(userId)).thenReturn(Optional.empty());
        when(itemStorage.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingStorage.findByBookerIdAndItemIdAndStatusAndEndBeforeOrderByStartDesc(eq(userId), eq(itemId),
                eq(BookingStatus.APPROVED),
                any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        NotFoundException userNotFound = assertThrows(NotFoundException.class,
                () -> itemService.addComment(userId, itemId, request));
        assertEquals("Пользователь с идентификатором " + userId + " не найден", userNotFound.getMessage());
        verify(commentStorage, never()).save(any(Comment.class));
    }

    @Test
    void addComment_NotFoundItem() {
        long userId = 1L;
        long itemId = 1L;
        NewCommentRequest request = new NewCommentRequest();
        request.setText("text");
        Booking booking = new Booking(1L, LocalDateTime.of(2025, 9, 9, 12, 0),
                LocalDateTime.of(2025, 9, 9, 14, 0), item, user, BookingStatus.APPROVED);

        when(itemStorage.findById(itemId)).thenReturn(Optional.empty());
        when(bookingStorage.findByBookerIdAndItemIdAndStatusAndEndBeforeOrderByStartDesc(eq(userId), eq(itemId),
                eq(BookingStatus.APPROVED),
                any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        NotFoundException itemNotFound = assertThrows(NotFoundException.class,
                () -> itemService.addComment(userId, itemId, request));
        assertEquals("Предмет с идентификатором " + itemId + " не найден", itemNotFound.getMessage());
        verify(commentStorage, never()).save(any(Comment.class));
    }

    @Test
    void addComment_NotFoundBooking() {
        long userId = 1L;
        long itemId = 1L;
        NewCommentRequest request = new NewCommentRequest();
        request.setText("text");

        when(bookingStorage.findByBookerIdAndItemIdAndStatusAndEndBeforeOrderByStartDesc(eq(userId), eq(itemId),
                eq(BookingStatus.APPROVED),
                any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        assertThrows(ValidationException.class, () -> itemService.addComment(userId, itemId, request));
        verify(commentStorage, never()).save(any(Comment.class));
    }
}
