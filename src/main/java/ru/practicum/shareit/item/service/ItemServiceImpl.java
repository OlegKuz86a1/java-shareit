package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;

import ru.practicum.shareit.item.dto.RequestItem;
import ru.practicum.shareit.item.dto.ResponseItem;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;

import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.common.exception.AccessDeniedException;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final ItemMapper itemMapper;

    @Override
    public List<ItemDto> allItems(Long userId) {
        checkUserExists(userId);
        return itemMapper.toDtoList(itemStorage.getAllByUserId(userId));
    }

    @Override
    public ItemDto getItem(Long userId, Long itemId) {
        checkUserExists(userId);
        Item item = itemStorage.getById(itemId);

        if (!item.getOwnerId().equals(userId)) {
            throw new AccessDeniedException("User %d isn't owner of item %d".formatted(userId, itemId));
        }
        return itemMapper.toDto(item);
    }

    @Override
    public List<ResponseItem> getItemsForRent(Long userId, String text) {
        checkUserExists(userId);
        return itemMapper.mapItemToResponseItem(itemStorage.getAllAvailableByText(text));
    }

    @Override
    public ItemDto addItem(ItemCreateDto itemCreateDto) {
        checkUserExists(itemCreateDto.getOwnerId());
        return itemMapper.toDto(itemStorage.create(itemMapper.mapToItem(itemCreateDto)));
    }

    @Override
    public ItemDto updateItem(Long userId, RequestItem requestItem) {
        checkUserExists(userId);
        Item oldItem = itemStorage.getById(requestItem.getId());
        if (!Objects.equals(userId, oldItem.getOwnerId())) {
            throw new NotFoundException("only the owner can change a item");
        }
        if (requestItem.getName() == null) {
            requestItem.setName(oldItem.getName());
        }
        if (requestItem.getDescription() == null) {
            requestItem.setDescription(oldItem.getDescription());
        }
        if (requestItem.getIsAvailable() == null) {
            requestItem.setIsAvailable(oldItem.isAvailable());
        }
        return  itemMapper.toDto(itemStorage.update(requestItem.getId(), itemMapper.mapRequestItemToItem(requestItem)));
    }

    private void checkUserExists(Long userId) {
        if (userStorage.getById(userId) == null) {
            throw new NotFoundException("user not found");
        }
    }
}
