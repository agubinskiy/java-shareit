package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {
    @NotNull(message = "Дата начала должна быть заполнена")
    private LocalDateTime start;

    @NotNull(message = "Дата окончания должна быть заполнена")
    @Future
    private LocalDateTime end;

    @NotNull(message = "Id предмета должен быть заполнен")
    private Long itemId;
}