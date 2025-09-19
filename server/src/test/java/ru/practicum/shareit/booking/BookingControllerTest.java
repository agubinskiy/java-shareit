package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingRequest;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingState;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.constants.Constants.USER_ID;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private final User user = new User(1L, "Name", "email@email.ru");
    private final Item item = new Item(1L, "itemName", "itemDescription", true, 1L, null);
    private final BookingDto bookingDto = new BookingDto(1L,
            LocalDateTime.now(),
            LocalDateTime.now().plusMinutes(1),
            ItemMapper.mapToItemElementDto(item),
            UserMapper.mapToUserElementDto(user),
            BookingStatus.WAITING);

    private final BookingDto appprovedBookingDto = new BookingDto(1L,
            LocalDateTime.now(),
            LocalDateTime.now().plusMinutes(1),
            ItemMapper.mapToItemElementDto(item),
            UserMapper.mapToUserElementDto(user),
            BookingStatus.APPROVED);

    @Test
    @SneakyThrows
    void getBooking() {
        long bookingId = 1L;
        long userId = 1L;

        mvc.perform(get("/bookings/{id}", bookingId)
                        .header(USER_ID, userId))
                .andExpect(status().isOk());

        verify(bookingService).getBooking(bookingId, userId);
    }

    @Test
    @SneakyThrows
    void getUsersBookings() {
        long userId = 1L;

        mvc.perform(get("/bookings")
                        .header(USER_ID, userId)
                        .param("state", "ALL"))
                .andExpect(status().isOk());

        verify(bookingService).getUsersBooking(userId, BookingState.ALL);
    }

    @Test
    @SneakyThrows
    void getUsersItemsBookings() {
        long userId = 1L;

        mvc.perform(get("/bookings/owner")
                        .header(USER_ID, userId)
                        .param("state", "ALL"))
                .andExpect(status().isOk());

        verify(bookingService).getUsersItemsBooking(userId, BookingState.ALL);
    }

    @Test
    @SneakyThrows
    void addBooking() {
        long userId = 1L;

        NewBookingRequest request = new NewBookingRequest();
        request.setStart(LocalDateTime.now());
        request.setEnd(LocalDateTime.now().plusMinutes(1));
        request.setItemId(1L);

        when(bookingService.addBooking(userId, request))
                .thenReturn(bookingDto);

        String result = mvc.perform(post("/bookings")
                        .header(USER_ID, userId)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(bookingDto), result);
    }

    @Test
    @SneakyThrows
    void approveBooking() {
        long userId = 1L;
        long bookingId = 1L;

        when(bookingService.approveBooking(userId, bookingId, true))
                .thenReturn(appprovedBookingDto);

        String result = mvc.perform(patch("/bookings/{id}", bookingId)
                        .contentType("application/json")
                        .header(USER_ID, userId)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(appprovedBookingDto), result);
    }


}
