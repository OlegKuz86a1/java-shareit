package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.RequestItem;
import ru.practicum.shareit.item.dto.ResponseItem;
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
    List<ItemDto> allItems(@RequestHeader(SHARER_USER_ID_HEADER) Long ownerId) {
        log.info("returning the list of items");
        return itemService.allItems(ownerId);
    }

    @GetMapping("/{itemId}")
    ItemDto getItem(@RequestHeader(SHARER_USER_ID_HEADER) Long ownerId, @PathVariable Long itemId) {
        log.info("returning the item");
        return itemService.getItem(ownerId, itemId);
    }

    @GetMapping("/search")
    List<ResponseItem> getItemsForRent(@RequestHeader(SHARER_USER_ID_HEADER) Long ownerId,
                                       @RequestParam String text) {
        log.info("returning a list of items ready for rent");
        return itemService.getItemsForRent(ownerId, text);
    }

    @PostMapping
    ItemDto create(@Positive @RequestHeader(SHARER_USER_ID_HEADER) Long ownerId, @Valid @RequestBody ItemCreateDto itemCreateDto) {
        log.info("returning the item added for rent: {}", itemCreateDto);
        itemCreateDto.setOwnerId(ownerId);
        return itemService.addItem(itemCreateDto);
    }

    @PatchMapping("/{itemId}")
    ItemDto update(@Positive @RequestHeader(SHARER_USER_ID_HEADER) Long ownerId,
                   @Positive @PathVariable Long itemId, @RequestBody RequestItem requestItem) {
        log.info("returning the updated item: {}", requestItem);
        requestItem.setId(itemId);
        return itemService.updateItem(ownerId, requestItem);
    }
}
