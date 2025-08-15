package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.errors.exceptions.ForbiddenException;
import ru.practicum.shareit.errors.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Autowired
    public ItemServiceImpl(ItemStorage itemStorage, UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    public List<ItemDto> getAllItems(long ownerId) {
        log.info("Выполняется запрос всех предметов");
        return itemStorage.getAllItems().stream()
                .filter(item -> item.getOwner().equals(ownerId))
                .map(ItemMapper::toItemDto)
                .toList();
    }

    public ItemDto getItemById(long id) {
        log.info("Выполняется запрос предмета с id={}", id);
        return itemStorage.getItem(id)
                .map(ItemMapper::toItemDto)
                .orElseThrow(() -> new NotFoundException("Предмет с id " + id + " не найден"));
    }

    public List<ItemDto> searchByText(String text) {
        log.info("Выполняется поиск предметов, текст запроса: {}", text);
        return itemStorage.searchByText(text).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    public ItemDto addItem(long ownerId, Item item) {
        log.info("Начинается добавление предмета {}", item);
        checkUser(ownerId);
        item.setOwner(ownerId);
        itemStorage.addItem(item);
        return ItemMapper.toItemDto(item);
    }

    public ItemDto updateItem(long ownerId, long itemId, Item item) {
        log.info("Начинается обновление предмета id={}", item.getId());
        if (!itemStorage.getItem(itemId).orElseThrow().getOwner().equals(ownerId)) {
            throw new ForbiddenException("Недостаточно прав");
        }
        return ItemMapper.toItemDto(itemStorage.updateItem(itemId, item));
    }

    private void checkUser(long id) {
        if (userStorage.getUserById(id).isEmpty()) {
            throw new NotFoundException("Пользователя с id " + id + " не существует");
        }
    }
}
