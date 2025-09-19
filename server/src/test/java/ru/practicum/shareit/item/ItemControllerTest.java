package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.constants.Constants.USER_ID;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mvc;

    private final ItemDto itemDto = new ItemDto(1L, "Name", "Description", true, 1L, 1L);
    private final CommentDto commentDto = new CommentDto(1L, "text", "author", LocalDateTime.now());


    @Test
    @SneakyThrows
    void getAllItems() {
        long userId = 1L;

        mvc.perform(get("/items")
                        .header(USER_ID, userId))
                .andExpect(status().isOk());

        verify(itemService).getAllItems(userId);
    }

    @Test
    @SneakyThrows
    void getItem() {
        long itemId = 1L;

        mvc.perform(get("/items/{id}", itemId))
                .andExpect(status().isOk());

        verify(itemService).getItemById(itemId);
    }

    @Test
    @SneakyThrows
    void searchItems() {
        String text = "text";

        mvc.perform(get("/items/search")
                        .param("text", text))
                .andExpect(status().isOk());

        verify(itemService).searchByText(text);
    }

    @Test
    @SneakyThrows
    void addItem() {
        long userId = 1L;
        NewItemRequest request = new NewItemRequest();
        request.setName("Name");
        request.setDescription("Description");
        request.setAvailable(true);

        when(itemService.addItem(userId, request))
                .thenReturn(itemDto);

        String result = mvc.perform(post("/items")
                        .header(USER_ID, userId)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(itemDto), result);
    }

    @Test
    @SneakyThrows
    void updateItem() {
        long userId = 1L;
        long itemId = 1L;
        NewItemRequest request = new NewItemRequest();
        request.setName("Name2");
        request.setDescription("Description2");
        request.setAvailable(true);

        ItemDto updatedItemDto = new ItemDto(itemId, "Name2", "Description2", true, userId, null);

        when(itemService.updateItem(userId, itemId, request))
                .thenReturn(updatedItemDto);

        String result = mvc.perform(patch("/items/{id}", itemId)
                        .header(USER_ID, userId)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(updatedItemDto), result);
    }

    @Test
    @SneakyThrows
    void addComment() {
        long userId = 1L;
        long itemId = 1L;
        NewCommentRequest request = new NewCommentRequest();
        request.setText("text");

        when(itemService.addComment(userId, itemId, request))
                .thenReturn(commentDto);

        String result = mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(USER_ID, userId)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(commentDto), result);
    }
}
