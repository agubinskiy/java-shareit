package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;


@Controller
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private final ItemClient itemClient;
    private static final String USER_ID = "X-Sharer-User-Id";

    @Autowired
    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @GetMapping
    public ResponseEntity<Object> getAllItems(@RequestHeader(USER_ID) Long owner) {
        return itemClient.getAllItems(owner);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable Long itemId) {
        log.info("Выполняется запрос предмета с id={}", itemId);
        return itemClient.getItem(itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text) {
        log.info("Выполняется поиск предметов, текст запроса: {}", text);
        return itemClient.searchItems(text);
    }

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader(USER_ID) Long ownerId,
                                          @Valid @RequestBody ItemRequestDto item) {
        log.info("Начинается добавление предмета {}, владелец {}", item, ownerId);
        return itemClient.addItem(ownerId, item);
    }


    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(USER_ID) Long ownerId, @PathVariable Long itemId,
                                             @RequestBody ItemRequestDto item) {
        log.info("Начинается обновление предмета id={}", itemId);
        return itemClient.updateItem(ownerId, itemId, item);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(USER_ID) Long userId, @PathVariable Long itemId,
                                             @Valid @RequestBody CommentRequestDto request) {
        log.info("Начинается добавление комментария {} предмету {} пользователем {}", request, itemId, userId);
        return itemClient.addComment(userId, itemId, request);
    }
}
