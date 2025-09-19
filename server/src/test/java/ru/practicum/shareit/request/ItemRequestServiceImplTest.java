package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.errors.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersDto;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {
    @Mock
    private ItemRequestStorage itemRequestStorage;

    @Mock
    private ItemStorage itemStorage;

    @Mock
    private UserStorage userStorage;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private final LocalDateTime baseTime = LocalDateTime.of(2025, 9, 9, 12, 0);
    private final ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "description", 1L,
            baseTime);
    private final User user = new User(1L, "Name", "email@email.ru");

    @Test
    void addItemRequest() {
        long userId = 1L;
        ItemRequest itemRequestToReturn = new ItemRequest(1L, "description", user,
                baseTime);
        NewRequest request = new NewRequest();
        request.setDescription("description");

        when(itemRequestStorage.save(any(ItemRequest.class))).thenReturn(itemRequestToReturn);
        when(userStorage.findById(userId)).thenReturn(Optional.of(user));

        ItemRequestDto actualItemRequestDto = itemRequestService.addItemRequest(userId, request);

        assertEquals(itemRequestDto, actualItemRequestDto);
        verify(itemRequestStorage).save(any(ItemRequest.class));
    }

    @Test
    void addItemRequest_UserNotFound() {
        long userId = 1L;
        NewRequest request = new NewRequest();
        request.setDescription("description");

        when(userStorage.findById(userId)).thenReturn(Optional.empty());


        assertThrows(NotFoundException.class, () -> itemRequestService.addItemRequest(userId, request));
    }

    @Test
    void getAllItemRequests() {
        ItemRequest itemRequestToReturn = new ItemRequest(1L, "description", user,
                baseTime);
        List<ItemRequest> listToReturn = List.of(itemRequestToReturn);

        when(itemRequestStorage.findAll()).thenReturn(listToReturn);

        List<ItemRequestDto> result = itemRequestService.getAllItemRequests();

        assertEquals(List.of(itemRequestDto), result);
    }

    @Test
    void getAllItemRequestsByUserId() {
        long userId = 1L;
        ItemRequest itemRequestToReturn = new ItemRequest(1L, "description", user,
                baseTime);
        Item itemToReturn = new Item(1L, "Name", "description", true, 1L, 1L);
        List<ItemRequest> listToReturn = List.of(itemRequestToReturn);
        List<Item> itemsToReturn = List.of(itemToReturn);
        List<ItemDto> itemsDtoToReturn = List.of(ItemMapper.toItemDto(itemToReturn));
        ItemRequestWithAnswersDto itemRequestWithAnswersDto = new ItemRequestWithAnswersDto(1L,
                "description", userId, baseTime, itemsDtoToReturn);

        when(itemRequestStorage.findByRequesterId(userId)).thenReturn(listToReturn);
        when(itemStorage.findByRequestIdIn(anySet())).thenReturn(itemsToReturn);

        List<ItemRequestWithAnswersDto> result = itemRequestService.getAllItemRequestsByUserId(userId);

        assertEquals(List.of(itemRequestWithAnswersDto), result);
    }

    @Test
    void getItemRequestById() {
        long itemRequestId = 1L;
        ItemRequest itemRequestToReturn = new ItemRequest(1L, "description", user,
                baseTime);
        Item itemToReturn = new Item(1L, "Name", "description", true, 1L, 1L);
        List<Item> itemsToReturn = List.of(itemToReturn);
        List<ItemDto> itemsDtoToReturn = List.of(ItemMapper.toItemDto(itemToReturn));
        ItemRequestWithAnswersDto itemRequestWithAnswersDto = new ItemRequestWithAnswersDto(1L,
                "description", 1L, baseTime, itemsDtoToReturn);

        when(itemRequestStorage.findById(itemRequestId)).thenReturn(Optional.of(itemRequestToReturn));
        when(itemStorage.findByRequestIdIn(anySet())).thenReturn(itemsToReturn);

        ItemRequestWithAnswersDto result = itemRequestService.getItemRequestById(itemRequestId);

        assertEquals(itemRequestWithAnswersDto, result);
    }

    @Test
    void getItemRequestById_NotFound() {
        long itemRequestId = 1L;

        when(itemRequestStorage.findById(itemRequestId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequestById(itemRequestId));
    }
}
