package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;

import java.util.ArrayList;
import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient (@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory((factory) -> new HttpComponentsClientHttpRequestFactory())
                .build()
        );
    }

    public ResponseEntity<Object> create(long userId, ItemCreateDto itemCreateDto) {
        return post("", userId, itemCreateDto);
    }

    public ResponseEntity<Object> update(long userId, long itemId, ItemCreateDto itemCreateDto) {
        return patch("/" + itemId, userId, itemCreateDto);
    }

    public ResponseEntity<Object> addComment(long userId, CreateCommentDto createCommentDto, long itemId) {
        return post("/" + itemId + "/comment", userId, createCommentDto);
    }

    public ResponseEntity<Object> getItem(long userId, long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getItemsForRent(long userId, String text) {
        if (text.isEmpty() || text.isBlank())
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ArrayList<>());

        Map<String, Object> parameters = Map.of("text", text);

        return get("/search?text={text}", userId, parameters);
    }

    public ResponseEntity<Object> allItems(long ownerId, int from, int size) {
        Map<String, Object> parameters = Map.of("from", from, "size", size);

        return get("?from={from}&size={size}", ownerId, parameters);
    }
}
