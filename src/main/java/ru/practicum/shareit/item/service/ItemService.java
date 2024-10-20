package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {

    List<ItemDtoAllFields> allItems(Long userId, int from, int size);

    ItemDtoAllFields getItem(Long userId, Long itemId);

    List<ResponseItem> getItemsForRent(Long userId, String text);

    ItemDto addItem(ItemCreateDto itemCreateDto);

    ItemDto updateItem(Long userId, RequestItem requestItem);

    CommentDto addComment(CreateCommentDto createCommentDto);

}
