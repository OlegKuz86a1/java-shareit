package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

 private final ItemService itemService;
 private static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";

    @GetMapping
    List<ItemDtoAllFields> allItems(@RequestHeader(SHARER_USER_ID_HEADER) Long ownerId,
                                    @RequestParam(defaultValue = "1") int  from,
                                    @RequestParam(defaultValue = "10") int size) {
        log.info("request for getting items by ownerId {}", ownerId);
        List<ItemDtoAllFields> items = itemService.allItems(ownerId, from, size);
        log.info("returning the list of items = {}", items);
        return items;
    }

    @GetMapping("/{itemId}")
    ItemDtoAllFields getItem(@RequestHeader(SHARER_USER_ID_HEADER) Long ownerId, @PathVariable Long itemId) {
        log.info("request for getting item by ID {}", itemId);
        ItemDtoAllFields item = itemService.getItem(ownerId, itemId);
        log.info("returning the item = {}", item);
        return item;
    }

    @GetMapping("/search")
    List<ResponseItem> getItemsForRent(@RequestHeader(SHARER_USER_ID_HEADER) Long ownerId,
                                       @RequestParam String text) {
        log.info("request for getting items by text {}", text);
        List<ResponseItem> itemsForRent = itemService.getItemsForRent(ownerId, text);
        log.info("returning a list of items ready for rent = {}", itemsForRent);
        return itemsForRent;
    }

    @PostMapping
    ItemDto create(@Positive @RequestHeader(SHARER_USER_ID_HEADER) Long ownerId,
                   @Valid @RequestBody ItemCreateDto itemCreateDto) {
        log.info("request for item added for rent: {}", itemCreateDto);
        itemCreateDto.setOwnerId(ownerId);
        ItemDto itemDto = itemService.addItem(itemCreateDto);
        log.info("returning the item added for rent: {}", itemCreateDto);
        return itemDto;
    }

    @PatchMapping("/{itemId}")
    ItemDto update(@Positive @RequestHeader(SHARER_USER_ID_HEADER) Long ownerId,
                   @Positive @PathVariable Long itemId,
                   @RequestBody RequestItem requestItem) {
        log.info("request for updated item {}", requestItem);
        requestItem.setId(itemId);
        ItemDto itemDto = itemService.updateItem(ownerId, requestItem);
        log.info("returning the updated item: {}", requestItem);
        return itemDto;
    }

    @PostMapping("/{itemId}/comment")
    CommentDto addComment(@RequestHeader(SHARER_USER_ID_HEADER) Long authorId,
                          @RequestBody CreateCommentDto createCommentDto,
                          @PathVariable Long itemId) {
        log.info("returning the comment added before query: {}", createCommentDto);
        createCommentDto.setAuthorId(authorId);
        createCommentDto.setItemId(itemId);
        CommentDto commentDto = itemService.addComment(createCommentDto);
        log.info("returning the comment added after query: {}", commentDto);
        return commentDto;
    }

}
