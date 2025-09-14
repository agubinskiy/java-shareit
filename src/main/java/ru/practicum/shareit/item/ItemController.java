package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;


@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private final ItemService itemService;
    private static final String USER_ID = "X-Sharer-User-Id";

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<ItemWithBookingDto> getAllItems(@RequestHeader(USER_ID) Long owner) {
        return itemService.getAllItems(owner);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingDto getItem(@PathVariable Long itemId) {
        log.info("Выполняется запрос предмета с id={}", itemId);
        return itemService.getItemById(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.info("Выполняется поиск предметов, текст запроса: {}", text);
        return itemService.searchByText(text);
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader(USER_ID) Long ownerId,
                           @Valid @RequestBody NewItemRequest item) {
        log.info("Начинается добавление предмета {}, владелец {}", item, ownerId);
        return itemService.addItem(ownerId, item);
    }


    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(USER_ID) Long ownerId, @PathVariable Long itemId,
                              @RequestBody NewItemRequest item) {
        log.info("Начинается обновление предмета id={}", itemId);
        return itemService.updateItem(ownerId, itemId, item);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(USER_ID) Long userId, @PathVariable Long itemId,
                                 @Valid @RequestBody NewCommentRequest request) {
        log.info("Начинается добавление комментария {} предмету {} пользователем {}", request, itemId, userId);
        return itemService.addComment(userId, itemId, request);
    }
}
