package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersDto;
import ru.practicum.shareit.request.dto.NewRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addItemRequest(long userId, NewRequest request);

    List<ItemRequestDto> getAllItemRequests();

    List<ItemRequestWithAnswersDto> getAllItemRequestsByUserId(Long userId);

    ItemRequestWithAnswersDto getItemRequestById(Long id);
}
