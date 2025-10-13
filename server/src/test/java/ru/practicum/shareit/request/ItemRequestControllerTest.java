package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.constants.Constants.USER_ID;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;

    private final ItemRequestDto itemRequestDto = new ItemRequestDto(1L,
            "Description",
            1L,
            LocalDateTime.now());

    @Test
    @SneakyThrows
    void getAllItemRequests() {
        mvc.perform(get("/requests/all"))
                .andExpect(status().isOk());

        verify(itemRequestService).getAllItemRequests();
    }

    @Test
    @SneakyThrows
    void getAllItemRequestsByUser() {
        long userId = 1L;

        mvc.perform(get("/requests")
                        .header(USER_ID, userId))
                .andExpect(status().isOk());

        verify(itemRequestService).getAllItemRequestsByUserId(userId);
    }

    @Test
    @SneakyThrows
    void getItemRequestById() {
        long requestId = 1L;

        mvc.perform(get("/requests/{id}", requestId))
                .andExpect(status().isOk());

        verify(itemRequestService).getItemRequestById(requestId);
    }

    @Test
    @SneakyThrows
    void addItemRequest() {
        long userId = 1L;
        NewRequest request = new NewRequest();
        request.setDescription("Description");

        when(itemRequestService.addItemRequest(userId, request))
                .thenReturn(itemRequestDto);

        String result = mvc.perform(post("/requests")
                        .contentType("application/json")
                        .header(USER_ID, userId)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(itemRequestDto), result);
    }
}
