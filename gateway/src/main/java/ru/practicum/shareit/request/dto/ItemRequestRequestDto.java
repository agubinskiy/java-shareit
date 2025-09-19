package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ItemRequestRequestDto {
    @NotBlank
    @Size(max = 512)
    private String description;
}
