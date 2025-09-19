package ru.practicum.shareit.item.mapper;


import ru.practicum.shareit.booking.dto.ItemBookingInfo;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemElementDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public class ItemMapper {
    public static ItemWithBookingDto toItemWithBookingDto(Item item, ItemBookingInfo bookingInfo,
                                                          List<Comment> comments) {
        return new ItemWithBookingDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwnerId(),
                item.getRequestId() != null ? item.getRequestId() : null,
                bookingInfo != null ? bookingInfo.getLastBooking() : null,
                bookingInfo != null ? bookingInfo.getNextBooking() : null,
                comments
        );
    }

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwnerId(),
                item.getRequestId() != null ? item.getRequestId() : null
        );
    }

    public static ItemElementDto mapToItemElementDto(Item item) {
        return new ItemElementDto(item.getId(), item.getName(), item.getDescription());
    }

    public static Item mapFromNewItemRequest(NewItemRequest request, long ownerId) {
        return new Item(
                null,
                request.getName(),
                request.getDescription(),
                request.getAvailable(),
                ownerId,
                request.getRequestId() != null ? request.getRequestId() : null
        );
    }

}
