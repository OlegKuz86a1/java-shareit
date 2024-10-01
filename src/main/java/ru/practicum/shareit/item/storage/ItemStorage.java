package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    List<Item> getAllByUserId(long userId);

    List<Item> getAllAvailableByText(String text);

    Item getById(long itemId);

    Item create(Item item);

    Item update(long id, Item item);

}
