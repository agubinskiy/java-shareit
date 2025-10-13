package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersDto;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static ru.practicum.shareit.constants.Constants.USER_ID;


@RestController
@RequestMapping(path = "/requests")
@Slf4j
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @GetMapping
    public List<ItemRequestWithAnswersDto> getAllItemRequests(@RequestHeader(USER_ID) Long userId) {
        log.info("Запрашивается список всех запросов вещей пользователя id={}", userId);
        return itemRequestService.getAllItemRequestsByUserId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequests() {
        log.info("Запрашивается список всех запросов вещей");
        return itemRequestService.getAllItemRequests();
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithAnswersDto getItemRequestById(@PathVariable Long requestId) {
        log.info("Запрашивается информация по запросу id={}", requestId);
        return itemRequestService.getItemRequestById(requestId);
    }

    @PostMapping
    public ItemRequestDto addItemRequest(@RequestHeader(USER_ID) Long userId, @RequestBody NewRequest request) {
        log.info("Начинается добавление запроса {} пользователем {}", request, userId);
        return itemRequestService.addItemRequest(userId, request);
    }
}
