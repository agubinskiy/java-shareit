package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto addItem(long ownerId, Item item);

    ItemDto updateItem(long ownerId, long id, Item item);

    ItemDto getItemById(long id);

    List<ItemDto> getAllItems(long ownerId);

    List<ItemDto> searchByText(String text);
}
