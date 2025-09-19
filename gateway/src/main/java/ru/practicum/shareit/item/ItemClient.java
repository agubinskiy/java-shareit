package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> getAllItems(Long owner) {
        return get("", owner);
    }

    public ResponseEntity<Object> getItem(Long itemId) {
        return get("/" + itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(String text) {
        Map<String, Object> parameters = Map.of(
                "text", text
        );
        return get("/search?text={text}", null, parameters);
    }

    @PostMapping
    public ResponseEntity<Object> addItem(Long ownerId, ItemRequestDto requestDto) {
        return post("", ownerId, requestDto);
    }


    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(Long ownerId, Long itemId, ItemRequestDto requestDto) {
        return patch("/" + itemId, ownerId, requestDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(Long userId, Long itemId, CommentRequestDto requestDto) {
        return post("/" + itemId + "/comment", userId, requestDto);
    }
}
