package ru.practicum.shareit.booking.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.BookingStorage;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.errors.exceptions.ForbiddenException;
import ru.practicum.shareit.errors.exceptions.NotFoundException;
import ru.practicum.shareit.errors.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@Transactional
public class BookingServiceImpl implements BookingService {
    private final BookingStorage bookingStorage;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    @Autowired
    public BookingServiceImpl(BookingStorage bookingStorage, UserStorage userStorage, ItemStorage itemStorage) {
        this.bookingStorage = bookingStorage;
        this.userStorage = userStorage;
        this.itemStorage = itemStorage;
    }

    @Override
    @Transactional
    public BookingDto addBooking(long bookerId, NewBookingRequest request) {
        log.debug("Начинается добавление бронирования {}", request);
        Item item = itemStorage.findById(request.getItemId()).orElseThrow(
                () -> new NotFoundException("Нет предмета с идентификатором " + request.getItemId())
        );
        User booker = userStorage.findById(bookerId).orElseThrow(
                () -> new NotFoundException("Нет пользователя с идентификатором " + bookerId)
        );
        if (!request.getStart().isBefore(request.getEnd())) {
            throw new ValidationException("end", "Дата окончания должна быть после даты начала");
        }
        if (!item.getAvailable()) {
            throw new ValidationException("item", "Предмет недоступен для бронирования");
        }
        Booking booking = new Booking();
        booking.setStart(request.getStart());
        booking.setEnd(request.getEnd());
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        return BookingMapper.mapBookingToDto(bookingStorage.save(booking));
    }

    @Override
    @Transactional
    public BookingDto approveBooking(long bookingId, long ownerId, boolean approved) {
        log.debug("Начинается обновление статуса бронирования {}", bookingId);
        Booking booking = bookingStorage.findById(bookingId).orElseThrow(
                () -> new NotFoundException("Нет бронирования с идентификатором " + bookingId)
        );
        if (booking.getItem().getOwnerId() != ownerId) {
            throw new ForbiddenException("Пользователь " + ownerId +
                    " не имеет прав на обновление статуса бронирования " + bookingId);
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        Booking updatedBooking = bookingStorage.save(booking);
        return BookingMapper.mapBookingToDto(updatedBooking);
    }

    @Override
    public BookingDto getBooking(long userId, long bookingId) {
        log.debug("Запрашивается информация по бронированию id={}", bookingId);
        Booking booking = bookingStorage.findById(bookingId).orElseThrow(
                () -> new NotFoundException("Нет бронирования с идентификатором " + bookingId)
        );
        if ((booking.getItem().getOwnerId() != userId) && (booking.getBooker().getId() != userId)) {
            throw new ForbiddenException("Пользователь " + userId +
                    " не имеет прав на запрос информации о бронировании " + bookingId);
        }
        return BookingMapper.mapBookingToDto(booking);
    }

    @Override
    public List<BookingDto> getUsersBooking(long userId, BookingState state) {
        log.debug("Запрашивается список бронирований пользователя id={}", userId);
        checkUser(userId);
        return switch (state) {
            case ALL -> BookingMapper.mapBookingToDto(bookingStorage.findByBookerId(userId));
            case CURRENT -> BookingMapper.mapBookingToDto(
                    bookingStorage.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                            LocalDateTime.now(), LocalDateTime.now())
            );
            case PAST -> BookingMapper.mapBookingToDto(
                    bookingStorage.findByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now())
            );
            case FUTURE -> BookingMapper.mapBookingToDto(
                    bookingStorage.findByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now())
            );
            case WAITING -> BookingMapper.mapBookingToDto(
                    bookingStorage.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING)
            );
            case REJECTED -> BookingMapper.mapBookingToDto(
                    bookingStorage.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED)
            );
        };
    }

    @Override
    public List<BookingDto> getUsersItemsBooking(long userId, BookingState state) {
        log.debug("Запрашивается список бронирований вещей пользователя id={}", userId);
        checkUser(userId);
        return switch (state) {
            case ALL -> BookingMapper.mapBookingToDto(bookingStorage.findAllByOwnerId(userId));
            case CURRENT -> BookingMapper.mapBookingToDto(
                    bookingStorage.findCurrentByOwnerId(userId, LocalDateTime.now())
            );
            case PAST -> BookingMapper.mapBookingToDto(
                    bookingStorage.findPastByOwnerId(userId, LocalDateTime.now())
            );
            case FUTURE -> BookingMapper.mapBookingToDto(
                    bookingStorage.findFutureByOwnerId(userId, LocalDateTime.now())
            );
            case WAITING -> BookingMapper.mapBookingToDto(
                    bookingStorage.findByOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING)
            );
            case REJECTED -> BookingMapper.mapBookingToDto(
                    bookingStorage.findByOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED)
            );
        };
    }

    private void checkUser(long userId) {
        if (userStorage.findById(userId).isEmpty()) {
            throw new NotFoundException("Нет пользователя с идентификатором " + userId);
        }
    }
}
