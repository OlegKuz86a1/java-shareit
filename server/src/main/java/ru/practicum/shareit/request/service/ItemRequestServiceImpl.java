package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreate;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final RequestMapper requestMapper;
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto create(long userId, ItemRequestCreate itemRequestCreate) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%s not found", userId)));
        itemRequestCreate.setDateRequestCreated(LocalDateTime.now());
        return requestMapper.toDto(itemRequestRepository.save(requestMapper
                .mapRequestCreateToEntity(requestor, itemRequestCreate)));
    }

    @Override
    public List<ItemRequestDtoWithItems> getRequestsByUserId(long userId) {
        userVerification(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorIdOrderByDateRequestCreatedDesc(userId);

        return itemRequests.stream()
                .map(itemRequest -> requestMapper.mapToRequestWithItem(itemRequest,
                        itemRepository.findItemsByItemRequestId(itemRequest.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDtoWithItems getRequestById(long userId, long requestId) {
        userVerification(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException(String.format("Item request with id=%s not found", userId)));
        List<Item> itemsByItemRequestId = itemRepository.findItemsByItemRequestId(requestId);
        return requestMapper.mapToRequestWithItem(itemRequest, itemsByItemRequestId);
    }

    @Override
    public List<ItemRequestDtoWithItems> getAllRequests(long userId, int from, int size) {
        Pageable sort = PageRequest.of(from / size, size, Sort.Direction.DESC,
                "dateRequestCreated");
        return itemRequestRepository.findOtherUsersRequests(userId, sort).get()
                .map(itemRequest -> requestMapper.mapToRequestWithItem(itemRequest,
                        itemRepository.findItemsByItemRequestId(itemRequest.getId())))
                .collect(Collectors.toList());
    }

    private void userVerification(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id=%s not found", userId));
        }
    }
}
