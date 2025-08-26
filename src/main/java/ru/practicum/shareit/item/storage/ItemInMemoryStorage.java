package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class ItemInMemoryStorage implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    private long counter;

    public Item addItem(Item item) {
        item.setId(++counter);
        items.put(counter, item);
        log.info("Предмет с id={} успешно добавлен", counter);
        log.debug("Добавлен предмет {}", item);
        return item;
    }

    public Item updateItem(long id, Item item) {
        Item oldItem = items.get(id);
        if (item.getName() != null) {
            oldItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            oldItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            oldItem.setAvailable(item.getAvailable());
        }
        log.info("Предмет с id={} успешно обновлён", id);
        log.debug("Обновлён предмет {}", oldItem);
        return oldItem;
    }

    public Optional<Item> getItem(long id) {
        return Optional.ofNullable(items.get(id));
    }

    public Collection<Item> getAllItems() {
        return items.values();
    }

    public List<Item> searchByText(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return items.values().stream()
                .filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase())) &&
                        item.getAvailable())
                .toList();
    }
}
