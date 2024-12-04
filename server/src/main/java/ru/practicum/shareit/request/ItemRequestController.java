package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreate;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j

public class ItemRequestController {

    private final ItemRequestService itemRequestService;
    private static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto create(@RequestHeader(SHARER_USER_ID_HEADER) long userId,
                                @RequestBody ItemRequestCreate itemRequestCreate) {
        ItemRequestDto itemRequestDto = itemRequestService.create(userId, itemRequestCreate);
        log.info("request for rent with user id {}", itemRequestCreate);
        return itemRequestDto;

    }

    @GetMapping
    public List<ItemRequestDtoWithItems> getRequestsByUserId(@RequestHeader(SHARER_USER_ID_HEADER) long userId) {
        List<ItemRequestDtoWithItems> itemRequestDtoWithItems = itemRequestService.getRequestsByUserId(userId);
        log.info("list of requests by id {}", itemRequestDtoWithItems);
        return itemRequestDtoWithItems;
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoWithItems getRequestById(@RequestHeader(SHARER_USER_ID_HEADER) long userId, @PathVariable long requestId) {
        ItemRequestDtoWithItems itemRequestDtoWithItems = itemRequestService.getRequestById(userId, requestId);
        log.info("request by id {}", itemRequestDtoWithItems);
        return itemRequestDtoWithItems;
    }

    @GetMapping("/all")
    public List<ItemRequestDtoWithItems> getAllRequests(@RequestHeader(SHARER_USER_ID_HEADER) long userId,
                                                 @RequestParam(defaultValue = "1") int  from,
                                                 @RequestParam(defaultValue = "10") int size) {
        List<ItemRequestDtoWithItems> itemRequestDtoWithItems = itemRequestService.getAllRequests(userId, from, size);
        log.info("all requests, from={}, size={} user id {} ", from, size, userId);
        return itemRequestDtoWithItems;
    }
}
