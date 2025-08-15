package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.validation.CreateValidation;
import ru.practicum.shareit.validation.UpdateValidation;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
public class User {
    private Long id;

    @NotBlank(message = "Имя не может быть пустым", groups = CreateValidation.class)
    private String name;

    @Email(message = "Некорректный формат почты", groups = {CreateValidation.class, UpdateValidation.class})
    @NotBlank(message = "Почта не может быть пустой", groups = CreateValidation.class)
    private String email;
}
