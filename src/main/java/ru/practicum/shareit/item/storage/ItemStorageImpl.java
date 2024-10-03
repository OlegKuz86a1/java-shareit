package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.common.AbstractStorage;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Objects;

@Slf4j
@Repository
public class ItemStorageImpl extends AbstractStorage<Item> implements ItemStorage {

    @Override
    public List<Item> getAllByUserId(long userId) {
        return getAll().stream()
                .filter(item -> Objects.equals(item.getOwnerId(), userId))
                .toList();
    }

    @Override
    public List<Item> getAllAvailableByText(String text) {
        return getAll().stream()
                .filter(Item::isAvailable)
                .filter(item -> item.getName() != null && item.getName().equalsIgnoreCase(text)
                        || item.getDescription() != null && item.getDescription().equalsIgnoreCase(text))
                .toList();

    }
}
