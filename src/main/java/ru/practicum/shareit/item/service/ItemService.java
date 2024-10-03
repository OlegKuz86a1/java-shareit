package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.RequestItem;
import ru.practicum.shareit.item.dto.ResponseItem;

import java.util.List;

public interface ItemService {

    List<ItemDto> allItems(Long userId);

    ItemDto getItem(Long userId, Long itemId);

    List<ResponseItem> getItemsForRent(Long userId, String text);

    ItemDto addItem(ItemCreateDto itemCreateDto);

    ItemDto updateItem(Long userId, RequestItem requestItem);
}
