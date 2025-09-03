package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingState;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {
    private final BookingService bookingService;
    private static final String USER_ID = "X-Sharer-User-Id";

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader(USER_ID) Long userId,
                                 @PathVariable Long bookingId) {
        log.info("Выполняется запрос бронирования {} пользователем {}", bookingId, userId);
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getUsersBookings(@RequestHeader(USER_ID) Long userId,
                                             @RequestParam(defaultValue = "ALL") String state) {
        log.info("Выполняется запрос всех бронирований пользователя {}, фильтр {}", userId, state);
        return bookingService.getUsersBooking(userId, BookingState.valueOf(state));
    }

    @GetMapping("/owner")
    public List<BookingDto> getUsersItemsBookings(@RequestHeader(USER_ID) Long userId,
                                                  @RequestParam(defaultValue = "ALL") String state) {
        log.info("Выполняется запрос всех бронирований вещей пользователя {}, фильтр {}", userId, state);
        return bookingService.getUsersItemsBooking(userId, BookingState.valueOf(state));
    }

    @PostMapping
    public BookingDto addBooking(@RequestHeader(USER_ID) Long userId, @Valid @RequestBody NewBookingRequest request) {
        log.info("Начинается добавление нового бронирования {} пользователем {}", request, userId);
        return bookingService.addBooking(userId, request);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@RequestHeader(USER_ID) Long userId,
                                     @PathVariable Long bookingId,
                                     @RequestParam boolean approved) {
        log.info("Начинается обновление статуса бронирования {} пользователем {}. Статус подтверждения = {}",
                bookingId, userId, approved);
        return bookingService.approveBooking(bookingId, approved, userId);
    }
}
