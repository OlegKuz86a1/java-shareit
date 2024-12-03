package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreate;


@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final  RequestClient requestClient;
    private static final String SHARER_USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(SHARER_USER_ID_HEADER) long userId,
                                         @Valid @RequestBody ItemRequestCreate itemRequestCreate) {
        log.info("request for rent with user id {} and request {}", userId, itemRequestCreate);
        return requestClient.create(userId, itemRequestCreate);

    }

    @GetMapping
    public ResponseEntity<Object> getRequestsByUserId(@RequestHeader(SHARER_USER_ID_HEADER) long userId) {
        log.info("get a list of requests by id {}", userId);
        return requestClient.getRequestsByUserId(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(SHARER_USER_ID_HEADER) @Positive long userId,
                                                 @PathVariable @Positive long requestId) {
        log.info("get request by id {}", requestId);
        return requestClient.getRequestById(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(SHARER_USER_ID_HEADER) long userId,
                                                 @RequestParam(defaultValue = "1") @PositiveOrZero int  from,
                                                 @RequestParam(defaultValue = "10") @Positive @Min(1) int size) {
        log.info("getting all requests, from={}, size={} user id {} ", from, size, userId);
        return requestClient.getAllRequests(userId, from, size);
    }
}
