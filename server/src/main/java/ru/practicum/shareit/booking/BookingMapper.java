package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;

public class BookingMapper {
    public static BookingDto mapBookingToDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                ItemMapper.mapToItemElementDto(booking.getItem()),
                UserMapper.mapToUserElementDto(booking.getBooker()),
                booking.getStatus()
        );
    }

    public static List<BookingDto> mapBookingToDto(List<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::mapBookingToDto)
                .toList();
    }
}
