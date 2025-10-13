package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.errors.ErrorResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ErrorResponseTest {

    @Test
    void shouldCreateErrorResponse() {
        ErrorResponse response = new ErrorResponse("Ошибка", "Что-то пошло не так");

        assertEquals("Ошибка", response.error());
        assertEquals("Что-то пошло не так", response.description());
    }

    @Test
    void shouldHandleNullValues() {
        ErrorResponse response = new ErrorResponse(null, null);

        assertNull(response.error());
        assertNull(response.description());
    }
}
