package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;


@Slf4j
@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {

    private final ItemClient itemClient;
    private static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";

    @GetMapping
    public ResponseEntity<Object> allItems(@Positive @RequestHeader(SHARER_USER_ID_HEADER) Long ownerId,
                                    @RequestParam(defaultValue = "1") int  from,
                                    @RequestParam(defaultValue = "10") int size) {
        log.info("request for getting items by ownerId {}, from {}, size {}", ownerId, from, size);
        return itemClient.allItems(ownerId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader(SHARER_USER_ID_HEADER) Long ownerId, @Positive @PathVariable Long itemId) {
        log.info("request for getting item by ID {} and user id {}", itemId, ownerId);
        return itemClient.getItem(ownerId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemsForRent(@RequestHeader(SHARER_USER_ID_HEADER) Long ownerId,
                                       @RequestParam String text) {
        log.info("request for getting items by text {}. User id = {}", text, ownerId);
        return itemClient.getItemsForRent(ownerId, text);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Positive @RequestHeader(SHARER_USER_ID_HEADER) Long ownerId,
                   @Valid @RequestBody ItemCreateDto itemCreateDto) {
        log.info("request for item added for rent: {} and owner id {}", itemCreateDto, ownerId);
        return itemClient.create(ownerId, itemCreateDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@Positive @RequestHeader(SHARER_USER_ID_HEADER) Long ownerId,
                   @Positive @PathVariable Long itemId,
                   @RequestBody ItemCreateDto itemCreateDto) {
        log.info("request for updated item {} and id {}", itemCreateDto, itemId);
        return itemClient.update(ownerId, itemId, itemCreateDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(SHARER_USER_ID_HEADER) Long authorId,
                          @Valid @RequestBody CreateCommentDto createCommentDto,
                          @PathVariable Long itemId) {
        log.info("comment = {} for item id = {} from author id = {}", createCommentDto, itemId, authorId);

        return itemClient.addComment(authorId, createCommentDto, itemId);
    }

}
