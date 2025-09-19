package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;


@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;
    private static final String USER_ID = "X-Sharer-User-Id";

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(USER_ID) Long userId,
                                             @PathVariable Long bookingId) {
        log.info("Выполняется запрос бронирования {} пользователем {}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getUsersBookings(@RequestHeader(USER_ID) Long userId,
                                                   @RequestParam(defaultValue = "ALL") String stateParam) {
        log.info("Выполняется запрос всех бронирований пользователя {}, фильтр {}", userId, stateParam);
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        return bookingClient.getUsersBookings(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getUsersItemsBookings(@RequestHeader(USER_ID) Long userId,
                                                        @RequestParam(defaultValue = "ALL") String stateParam) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Выполняется запрос всех бронирований вещей пользователя {}, фильтр {}", userId, state);
        return bookingClient.getUsersItemsBookings(userId, state);
    }

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader(USER_ID) Long userId,
                                             @Valid @RequestBody BookItemRequestDto requestDto) {
        log.info("Начинается добавление нового бронирования {} пользователем {}", requestDto, userId);
        return bookingClient.addBooking(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader(USER_ID) Long userId,
                                                 @PathVariable Long bookingId,
                                                 @RequestParam Boolean approved) {
        log.info("Начинается обновление статуса бронирования {} пользователем {}. Статус подтверждения = {}",
                bookingId, userId, approved);
        return bookingClient.approveBooking(userId, bookingId, approved);
    }
}