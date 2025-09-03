package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.BookingStorage;
import ru.practicum.shareit.booking.dto.ItemBookingInfo;
import ru.practicum.shareit.errors.exceptions.ForbiddenException;
import ru.practicum.shareit.errors.exceptions.NotFoundException;
import ru.practicum.shareit.errors.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentStorage;
import ru.practicum.shareit.item.storage.ItemStorage;
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
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final BookingStorage bookingStorage;
    private final CommentStorage commentStorage;

    @Autowired
    public ItemServiceImpl(ItemStorage itemStorage, UserStorage userStorage,
                           BookingStorage bookingStorage, CommentStorage commentStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
        this.bookingStorage = bookingStorage;
        this.commentStorage = commentStorage;
    }

    @Override
    public List<ItemWithBookingDto> getAllItems(long ownerId) {
        log.debug("Выполняется запрос всех предметов");
        Map<Long, Item> itemMap = itemStorage.findByOwnerId(ownerId).stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));
        Map<Long, List<Comment>> commentMap = commentStorage.findByItemIdIn(itemMap.keySet())
                .stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));
        Map<Long, ItemBookingInfo> bookingInfoMap = bookingStorage.findItemBookingInfo(itemMap.keySet(),
                        LocalDateTime.now())
                .stream()
                .collect(Collectors.toMap(ItemBookingInfo::getItemId, Function.identity()));
        return itemMap.values().stream()
                .map(item -> {
                    ItemBookingInfo bookingInfo = bookingInfoMap.get(item.getId());
                    return new ItemWithBookingDto(item.getId(),
                            item.getName(),
                            item.getDescription(),
                            item.getAvailable(),
                            item.getOwnerId(),
                            bookingInfo != null ? bookingInfo.getLastBooking() : null,
                            bookingInfo != null ? bookingInfo.getNextBooking() : null,
                            commentMap.getOrDefault(item.getId(), Collections.emptyList()));
                })
                .toList();
    }

    @Override
    public ItemWithBookingDto getItemById(long id) {
        log.debug("Выполняется запрос предмета с id={}", id);
        Item item = itemStorage.findById(id).orElseThrow(
                () -> new NotFoundException("Предмет с id " + id + " не найден"));
        ItemBookingInfo bookingInfo = bookingStorage.findItemBookingInfo(Set.of(id), LocalDateTime.now())
                .stream()
                .findFirst()
                .orElse(null);
        List<Comment> comments = commentStorage.findByItemIdIn(Set.of(id));

        return ItemMapper.toItemWithBookingDto(item, null, comments);
    }

    @Override
    public List<ItemDto> searchByText(String text) {
        log.debug("Выполняется поиск предметов, текст запроса: {}", text);
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemStorage.searchByText(text).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    @Transactional
    public ItemDto addItem(long ownerId, Item item) {
        log.debug("Начинается добавление предмета {}", item);
        checkUser(ownerId);
        item.setOwnerId(ownerId);
        itemStorage.save(item);
        return ItemMapper.toItemDto(item);
    }


    @Override
    @Transactional
    public ItemDto updateItem(long ownerId, long itemId, Item item) {
        log.debug("Начинается обновление предмета id={}", item.getId());
        if (!itemStorage.findById(itemId).orElseThrow().getOwnerId().equals(ownerId)) {
            throw new ForbiddenException("Недостаточно прав");
        }
        Item oldItem = itemStorage.findById(itemId).orElseThrow(
                () -> new NotFoundException("Предмет с id=" + itemId + " не найден")
        );
        if (item.getName() != null) {
            oldItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            oldItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            oldItem.setAvailable(item.getAvailable());
        }
        log.info("Предмет с id={} успешно обновлён", itemId);
        log.debug("Обновлён предмет {}", oldItem);
        Item updatedItem = itemStorage.save(oldItem);
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    @Transactional
    public CommentDto addComment(long userId, long itemId, NewCommentRequest request) {
        log.debug("Начинается добавление комментария {} предмету {} пользователем {}", request, itemId, userId);
        if (bookingStorage.findByBookerIdAndItemIdAndStatusAndEndBeforeOrderByStartDesc(userId, itemId,
                BookingStatus.APPROVED, LocalDateTime.now()).isEmpty()) {
            throw new ValidationException("", "Не выполнены условия для возможности оставить комментарий");
        }
        Item item = itemStorage.findById(itemId).orElseThrow(
                () -> new NotFoundException("Предмета с таким идентификатором не найдено")
        );
        User author = userStorage.findById(userId).orElseThrow(
                () -> new NotFoundException("Нет пользователя с указанным идентификатором")
        );
        Comment comment = new Comment();
        comment.setText(request.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        commentStorage.save(comment);
        return CommentMapper.mapToCommentDto(comment);
    }

    private void checkUser(long id) {
        if (userStorage.findById(id).isEmpty()) {
            throw new NotFoundException("Пользователя с id " + id + " не существует");
        }
    }
}
