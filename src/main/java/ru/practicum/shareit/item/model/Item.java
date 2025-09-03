package ru.practicum.shareit.item.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;


@Data
@RequiredArgsConstructor
@Table(name = "items")
@Entity
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Название предмета не может быть пустым")
    @Size(max = 255)
    private String name;

    @NotBlank(message = "Описание не может быть пустым")
    @Size(max = 1024)
    private String description;

    @NotNull(message = "Должна быть проставлена доступность")
    private Boolean available;

    @Column(name = "owner_id")
    private Long ownerId;

    //TO DO
    //private ItemRequest request;
}
