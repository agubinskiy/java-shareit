package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    Item addItem(Item item);

    Item updateItem(long id, Item item);

    Optional<Item> getItem(long id);

    Collection<Item> getAllItems();

    List<Item> searchByText(String text);
}
