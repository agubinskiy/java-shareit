package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.practicum.shareit.user.validation.CreateValidation;
import ru.practicum.shareit.user.validation.UpdateValidation;

@Data
public class UserRequestDto {
    @NotBlank(message = "Имя не может быть пустым", groups = CreateValidation.class)
    @Size(max = 255)
    private String name;

    @Email(message = "Некорректный формат почты", groups = {CreateValidation.class, UpdateValidation.class})
    @NotBlank(message = "Почта не может быть пустой", groups = CreateValidation.class)
    @Size(max = 512)
    private String email;
}
