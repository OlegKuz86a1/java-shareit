package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreate;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(long userId, ItemRequestCreate itemRequestCreate);

    List<ItemRequestDtoWithItems> getRequestsByUserId(long userId);

    ItemRequestDtoWithItems getRequestById(long userId, long requestIs);

    List<ItemRequestDtoWithItems> getAllRequests(long userId, int from, int size);

}
