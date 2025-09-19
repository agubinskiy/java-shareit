package ru.practicum.shareit.booking.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;

import java.util.List;

@Transactional(readOnly = true)
public interface BookingService {
    @Transactional
    BookingDto addBooking(long booker, NewBookingRequest request);

    BookingDto approveBooking(long bookingId, long ownerId, boolean approved);

    BookingDto getBooking(long userId, long bookingId);

    List<BookingDto> getUsersBooking(long userId, BookingState state);

    List<BookingDto> getUsersItemsBooking(long userId, BookingState state);
}
