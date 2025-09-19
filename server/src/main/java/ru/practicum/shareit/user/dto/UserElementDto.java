package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserElementDto {
    private Long id;
    private String authorName;
    private String email;
}
