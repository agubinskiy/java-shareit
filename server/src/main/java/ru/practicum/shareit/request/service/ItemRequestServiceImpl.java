package ru.practicum.shareit.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.errors.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersDto;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestStorage itemRequestStorage;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestStorage itemRequestStorage,
                                  UserStorage userStorage,
                                  ItemStorage itemStorage) {
        this.itemRequestStorage = itemRequestStorage;
        this.userStorage = userStorage;
        this.itemStorage = itemStorage;
    }

    @Override
    public ItemRequestDto addItemRequest(long userId, NewRequest request) {
        log.debug("Начинается добавление запроса {}", request);
        User requester = userStorage.findById(userId).orElseThrow(
                () -> new NotFoundException("Нет пользователя с идентификатором " + userId)
        );
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(request.getDescription());
        itemRequest.setRequester(requester);
        itemRequest.setCreated(LocalDateTime.now());
        return RequestMapper.maptoItemRequestDto(itemRequestStorage.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests() {
        log.debug("Начинается поиск всех запросов вещей");
        return itemRequestStorage.findAll().stream()
                .map(RequestMapper::maptoItemRequestDto)
                .toList();
    }

    @Override
    public List<ItemRequestWithAnswersDto> getAllItemRequestsByUserId(Long userId) {
        log.debug("Начинается поиск запросов пользователя id={}", userId);
        Map<Long, ItemRequest> itemRequestsMap = itemRequestStorage.findByRequesterId(userId).stream()
                .collect(Collectors.toMap(ItemRequest::getId, Function.identity()));
        Map<Long, List<ItemDto>> answersMap = itemStorage.findByRequestIdIn(itemRequestsMap.keySet())
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.groupingBy(ItemDto::getId));
        return itemRequestsMap.values()
                .stream()
                .map(request -> RequestMapper.mapToItemRequestWithAnswersDto(
                        request,
                        answersMap.getOrDefault(request.getId(), Collections.emptyList())
                ))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestWithAnswersDto getItemRequestById(Long id) {
        log.debug("Начинается поиск запроса id={}", id);
        ItemRequest itemRequest = itemRequestStorage.findById(id).orElseThrow(
                () -> new NotFoundException("Не найден запрос с id=" + id)
        );
        List<ItemDto> answers = itemStorage.findByRequestIdIn(Set.of(id))
                .stream()
                .map(ItemMapper::toItemDto)
                .toList();
        return RequestMapper.mapToItemRequestWithAnswersDto(itemRequest, answers);
    }
}
