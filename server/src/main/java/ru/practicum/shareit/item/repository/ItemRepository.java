package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
@SuppressWarnings("unused")
public interface ItemRepository extends JpaRepository<Item, Integer> {

    List<Item> findAllByOwnerId(int ownerId, Pageable pageable);

    @Query("from Item where available is true and UPPER(CONCAT(name, description)) like UPPER(CONCAT('%',:text,'%'))")
    List<Item> findAllByNameOrDescription(String text, Pageable pageable);

}
