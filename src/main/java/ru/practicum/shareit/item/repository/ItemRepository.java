package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Override
    @EntityGraph("item.owner")
    Optional<Item> findById(Long id);

    @Query(value = "select i from Item i where i.isAvailable = true and (lower(i.name) like lower(:text) " +
            "or lower(i.description) like lower(:text))")
    List<Item> findItemsByText(@Param("text") String text);

    Page<Item> findItemsByOwnerId(Long userId, Pageable pageable);

}
