package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewItemRequest {
    @NotBlank(message = "Название предмета не может быть пустым")
    @Size(max = 255)
    private String name;

    @NotBlank(message = "Описание не может быть пустым")
    @Size(max = 1024)
    private String description;

    @NotNull(message = "Должна быть проставлена доступность")
    private Boolean available;
}
