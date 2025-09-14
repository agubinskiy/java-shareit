package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage extends JpaRepository<Item, Long> {
    @Query("select i " +
            "from Item i " +
            "where (LOWER(i.name) LIKE LOWER(CONCAT('%', ?1, '%'))" +
            "or LOWER(i.description) LIKE LOWER(CONCAT('%', ?1, '%')))" +
            "and i.available = TRUE"
    )
    List<Item> searchByText(String searchText);

    List<Item> findByOwnerId(long ownerId);
}
