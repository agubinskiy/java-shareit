package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.NewItemRequest;

import java.util.List;

public interface ItemService {
    ItemDto addItem(long ownerId, NewItemRequest item);

    ItemDto updateItem(long ownerId, long itemId, NewItemRequest item);

    ItemWithBookingDto getItemById(long id);

    List<ItemWithBookingDto> getAllItems(long ownerId);

    List<ItemDto> searchByText(String text);

    CommentDto addComment(long userId, long itemId, NewCommentRequest request);
}
