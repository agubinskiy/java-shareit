package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;


@Controller
@RequestMapping(path = "/requests")
@Slf4j
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;
    private static final String USER_ID = "X-Sharer-User-Id";

    @Autowired
    public ItemRequestController(ItemRequestClient itemRequestClient) {
        this.itemRequestClient = itemRequestClient;
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemRequests(@RequestHeader(USER_ID) Long userId) {
        log.info("Запрашивается список всех запросов вещей пользователя id={}", userId);
        return itemRequestClient.getAllItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests() {
        log.info("Запрашивается список всех запросов вещей");
        return itemRequestClient.getAllItemRequests();
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@PathVariable Long requestId) {
        log.info("Запрашивается информация по запросу id={}", requestId);
        return itemRequestClient.getItemRequestById(requestId);
    }

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@RequestHeader(USER_ID) Long userId,
                                                 @Valid @RequestBody ItemRequestRequestDto requestDto) {
        log.info("Начинается добавление запроса {} пользователем {}", requestDto, userId);
        return itemRequestClient.addItemRequest(userId, requestDto);
    }
}
