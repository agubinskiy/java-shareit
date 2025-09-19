package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.service.BookingState;
import ru.practicum.shareit.errors.exceptions.ForbiddenException;
import ru.practicum.shareit.errors.exceptions.NotFoundException;
import ru.practicum.shareit.errors.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {
    @Mock
    private BookingStorage bookingStorage;

    @Mock
    private UserStorage userStorage;

    @Mock
    private ItemStorage itemStorage;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private final LocalDateTime start = LocalDateTime.now().plusMinutes(10);
    private final LocalDateTime end = LocalDateTime.now().plusMinutes(30);
    private final User user = new User(1L, "Name", "email@email.ru");
    private final Item item = new Item(1L, "name", "description", true, 1L, 1L);
    private final Booking booking = new Booking(1L, start, end, item, user, BookingStatus.WAITING);
    BookingDto bookingDto = BookingMapper.mapBookingToDto(booking);

    @Test
    void addBooking() {
        long userId = 1L;
        long itemId = 1L;
        NewBookingRequest request = new NewBookingRequest();
        request.setItemId(itemId);
        request.setStart(start);
        request.setEnd(end);

        when(userStorage.findById(userId)).thenReturn(Optional.of(user));
        when(itemStorage.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingStorage.save(any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingService.addBooking(userId, request);

        assertEquals(bookingDto, result);
        verify(bookingStorage).save(any(Booking.class));
    }

    @Test
    void addBooking_UserNotFound() {
        long userId = 1L;
        long itemId = 1L;
        NewBookingRequest request = new NewBookingRequest();
        request.setItemId(itemId);
        request.setStart(start);
        request.setEnd(end);

        when(userStorage.findById(userId)).thenReturn(Optional.empty());
        when(itemStorage.findById(itemId)).thenReturn(Optional.of(item));

        NotFoundException userNotFound = assertThrows(NotFoundException.class,
                () -> bookingService.addBooking(userId, request));
        assertEquals("Нет пользователя с идентификатором " + userId, userNotFound.getMessage());
        verify(bookingStorage, never()).save(any(Booking.class));
    }

    @Test
    void addBooking_ItemNotFound() {
        long userId = 1L;
        long itemId = 1L;
        NewBookingRequest request = new NewBookingRequest();
        request.setItemId(itemId);
        request.setStart(start);
        request.setEnd(end);

        when(itemStorage.findById(itemId)).thenReturn(Optional.empty());

        NotFoundException itemNotFound = assertThrows(NotFoundException.class,
                () -> bookingService.addBooking(userId, request));
        assertEquals("Нет предмета с идентификатором " + itemId, itemNotFound.getMessage());
        verify(bookingStorage, never()).save(any(Booking.class));
    }

    @Test
    void addBooking_NotValidDate() {
        long userId = 1L;
        long itemId = 1L;
        NewBookingRequest request = new NewBookingRequest();
        request.setItemId(itemId);
        request.setStart(end);
        request.setEnd(start);

        when(userStorage.findById(userId)).thenReturn(Optional.of(user));
        when(itemStorage.findById(itemId)).thenReturn(Optional.of(item));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.addBooking(userId, request));
        assertEquals("end", exception.getParameter());
        assertEquals("Дата окончания должна быть после даты начала", exception.getReason());
        verify(bookingStorage, never()).save(any(Booking.class));
    }

    @Test
    void addBooking_NotValidItem() {
        long userId = 1L;
        long itemId = 2L;
        NewBookingRequest request = new NewBookingRequest();
        request.setItemId(itemId);
        request.setStart(start);
        request.setEnd(end);
        Item itemNoValid = new Item(2L, "name", "description", false, 1L, 1L);

        when(userStorage.findById(userId)).thenReturn(Optional.of(user));
        when(itemStorage.findById(itemId)).thenReturn(Optional.of(itemNoValid));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> bookingService.addBooking(userId, request));
        assertEquals("item", exception.getParameter());
        assertEquals("Предмет недоступен для бронирования", exception.getReason());
        verify(bookingStorage, never()).save(any(Booking.class));
    }

    @Test
    void approveBooking_approving() {
        long userId = 1L;
        long bookingId = 1L;

        when(bookingStorage.save(any(Booking.class))).thenReturn(booking);
        when(bookingStorage.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.approveBooking(bookingId, userId, true);

        assertEquals(BookingStatus.APPROVED, result.getStatus());
        verify(bookingStorage).save(any(Booking.class));
    }

    @Test
    void approveBooking_rejecting() {
        long userId = 1L;
        long bookingId = 1L;

        when(bookingStorage.save(any(Booking.class))).thenReturn(booking);
        when(bookingStorage.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.approveBooking(bookingId, userId, false);

        assertEquals(BookingStatus.REJECTED, result.getStatus());
        verify(bookingStorage).save(any(Booking.class));
    }

    @Test
    void approveBooking_NotFound() {
        long userId = 1L;
        long bookingId = 1L;

        when(bookingStorage.findById(bookingId)).thenReturn(Optional.empty());

        NotFoundException bookingNotFound = assertThrows(NotFoundException.class,
                () -> bookingService.approveBooking(bookingId, userId, false));
        assertEquals("Нет бронирования с идентификатором " + bookingId, bookingNotFound.getMessage());
        verify(bookingStorage, never()).save(any(Booking.class));
    }

    @Test
    void approveBooking_Forbidden() {
        long userId = 2L;
        long bookingId = 1L;

        when(bookingStorage.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(ForbiddenException.class, () -> bookingService.approveBooking(bookingId, userId, true));
        verify(bookingStorage, never()).save(any(Booking.class));
    }

    @Test
    void getBooking() {
        long userId = 1L;
        long bookingId = 1L;

        when(bookingStorage.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.getBooking(userId, bookingId);

        assertEquals(bookingDto, result);
    }

    @Test
    void getBooking_NotFound() {
        long userId = 1L;
        long bookingId = 1L;

        when(bookingStorage.findById(bookingId)).thenReturn(Optional.empty());

        NotFoundException bookingNotFound = assertThrows(NotFoundException.class,
                () -> bookingService.getBooking(userId, bookingId));
        assertEquals("Нет бронирования с идентификатором " + bookingId, bookingNotFound.getMessage());
    }

    @Test
    void getBooking_Forbidden() {
        long userId = 2L;
        long bookingId = 1L;

        when(bookingStorage.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(ForbiddenException.class, () -> bookingService.approveBooking(bookingId, userId, true));
    }

    @Test
    void getUsersBooking_All() {
        long userId = 1L;
        BookingState state = BookingState.ALL;

        when(bookingStorage.findByBookerId(userId)).thenReturn(List.of(booking));
        when(userStorage.findById(userId)).thenReturn(Optional.of(user));

        List<BookingDto> result = bookingService.getUsersBooking(userId, state);

        assertEquals(List.of(bookingDto), result);
    }

    @Test
    void getUsersBooking_Current() {
        long userId = 1L;
        BookingState state = BookingState.CURRENT;

        when(bookingStorage.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(eq(userId), any(LocalDateTime.class),
                any(LocalDateTime.class))).thenReturn(List.of(booking));
        when(userStorage.findById(userId)).thenReturn(Optional.of(user));

        List<BookingDto> result = bookingService.getUsersBooking(userId, state);

        assertEquals(List.of(bookingDto), result);
    }

    @Test
    void getUsersBooking_Past() {
        long userId = 1L;
        BookingState state = BookingState.PAST;

        when(bookingStorage.findByBookerIdAndEndBeforeOrderByStartDesc(eq(userId), any(LocalDateTime.class)
        )).thenReturn(List.of(booking));
        when(userStorage.findById(userId)).thenReturn(Optional.of(user));

        List<BookingDto> result = bookingService.getUsersBooking(userId, state);

        assertEquals(List.of(bookingDto), result);
    }

    @Test
    void getUsersBooking_Future() {
        long userId = 1L;
        BookingState state = BookingState.FUTURE;

        when(bookingStorage.findByBookerIdAndStartAfterOrderByStartDesc(eq(userId), any(LocalDateTime.class)
        )).thenReturn(List.of(booking));
        when(userStorage.findById(userId)).thenReturn(Optional.of(user));

        List<BookingDto> result = bookingService.getUsersBooking(userId, state);

        assertEquals(List.of(bookingDto), result);
    }

    @Test
    void getUsersBooking_Waiting() {
        long userId = 1L;
        BookingState state = BookingState.WAITING;

        when(bookingStorage.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING))
                .thenReturn(List.of(booking));
        when(userStorage.findById(userId)).thenReturn(Optional.of(user));

        List<BookingDto> result = bookingService.getUsersBooking(userId, state);

        assertEquals(List.of(bookingDto), result);
    }

    @Test
    void getUsersBooking_Rejected() {
        long userId = 1L;
        BookingState state = BookingState.REJECTED;

        when(bookingStorage.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED))
                .thenReturn(List.of(booking));
        when(userStorage.findById(userId)).thenReturn(Optional.of(user));

        List<BookingDto> result = bookingService.getUsersBooking(userId, state);

        assertEquals(List.of(bookingDto), result);
    }

    @Test
    void getUsersBooking_UserNotFound() {
        long userId = 1L;
        BookingState state = BookingState.ALL;

        when(userStorage.findById(userId)).thenReturn(Optional.empty());

        NotFoundException userNotFound = assertThrows(NotFoundException.class,
                () -> bookingService.getUsersBooking(userId, state));
        assertEquals("Нет пользователя с идентификатором " + userId, userNotFound.getMessage());
    }

    @Test
    void getUsersItemsBooking_All() {
        long userId = 1L;
        BookingState state = BookingState.ALL;

        when(bookingStorage.findAllByOwnerId(userId)).thenReturn(List.of(booking));
        when(userStorage.findById(userId)).thenReturn(Optional.of(user));

        List<BookingDto> result = bookingService.getUsersItemsBooking(userId, state);

        assertEquals(List.of(bookingDto), result);
    }

    @Test
    void getUsersItemsBooking_Current() {
        long userId = 1L;
        BookingState state = BookingState.CURRENT;

        when(bookingStorage.findCurrentByOwnerId(eq(userId), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));
        when(userStorage.findById(userId)).thenReturn(Optional.of(user));

        List<BookingDto> result = bookingService.getUsersItemsBooking(userId, state);

        assertEquals(List.of(bookingDto), result);
    }

    @Test
    void getUsersItemsBooking_Past() {
        long userId = 1L;
        BookingState state = BookingState.PAST;

        when(bookingStorage.findPastByOwnerId(eq(userId), any(LocalDateTime.class)
        )).thenReturn(List.of(booking));
        when(userStorage.findById(userId)).thenReturn(Optional.of(user));

        List<BookingDto> result = bookingService.getUsersItemsBooking(userId, state);

        assertEquals(List.of(bookingDto), result);
    }

    @Test
    void getUsersItemsBooking_Future() {
        long userId = 1L;
        BookingState state = BookingState.FUTURE;

        when(bookingStorage.findFutureByOwnerId(eq(userId), any(LocalDateTime.class)
        )).thenReturn(List.of(booking));
        when(userStorage.findById(userId)).thenReturn(Optional.of(user));

        List<BookingDto> result = bookingService.getUsersItemsBooking(userId, state);

        assertEquals(List.of(bookingDto), result);
    }

    @Test
    void getUsersItemsBooking_Waiting() {
        long userId = 1L;
        BookingState state = BookingState.WAITING;

        when(bookingStorage.findByOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING))
                .thenReturn(List.of(booking));
        when(userStorage.findById(userId)).thenReturn(Optional.of(user));

        List<BookingDto> result = bookingService.getUsersItemsBooking(userId, state);

        assertEquals(List.of(bookingDto), result);
    }

    @Test
    void getUsersItemsBooking_Rejected() {
        long userId = 1L;
        BookingState state = BookingState.REJECTED;

        when(bookingStorage.findByOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED))
                .thenReturn(List.of(booking));
        when(userStorage.findById(userId)).thenReturn(Optional.of(user));

        List<BookingDto> result = bookingService.getUsersItemsBooking(userId, state);

        assertEquals(List.of(bookingDto), result);
    }

    @Test
    void getUsersItemsBooking_UserNotFound() {
        long userId = 1L;
        BookingState state = BookingState.ALL;

        when(userStorage.findById(userId)).thenReturn(Optional.empty());

        NotFoundException userNotFound = assertThrows(NotFoundException.class,
                () -> bookingService.getUsersItemsBooking(userId, state));
        assertEquals("Нет пользователя с идентификатором " + userId, userNotFound.getMessage());
    }
}
