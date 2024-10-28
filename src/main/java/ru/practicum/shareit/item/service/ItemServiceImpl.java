package ru.practicum.shareit.item.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDateDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.common.exception.IllegalDataException;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.*;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;


import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.repository.UserRepository;


import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final EntityManager em;

    @Override
    public CommentDto addComment(CreateCommentDto createCommentDto) {
        Item item = itemRepository.findById(createCommentDto.getItemId()).orElseThrow(() ->
                new NotFoundException("Item not found with id: " + createCommentDto.getAuthorId()));
        if (bookingRepository.existsByItemIdAndStatusAndEndAfter(item.getId(), BookingStatus.APPROVED, LocalDateTime.now())) {
            throw new IllegalDataException("Item in approved booking");
        }
        Comment comment = new Comment();
        comment.setText(createCommentDto.getText());
        comment.setAuthor(userRepository.findById(createCommentDto.getAuthorId()).orElseThrow(() ->
                new NotFoundException("User not found with id: " + createCommentDto.getAuthorId())));
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
        return commentMapper.toDto(findById(commentRepository.save(comment).getId()));
    }

    public Comment findById(Long commentId) {
        em.clear();
        Comment comment = commentRepository.findById(commentId).orElseThrow();
        return comment;
    }

    @Override
    public List<ItemDtoAllFields> allItems(Long userId, int from, int size) {
        checkUserExists(userId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.Direction.DESC, "id");
        return itemRepository.findItemsByOwnerId(userId, pageable).get()
                .map(item -> itemMapper.mapToItemDtoWithDateBooking(item, lastBooking(item.getId()),
                        nearestBooking(item.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemDtoAllFields getItem(Long userId, Long itemId) {
       checkUserExists(userId);

       Item item = itemRepository.findById(itemId).orElseThrow(() ->
               new NotFoundException("Item not found with id: " + itemId));

       List<CommentDto> commentDtos = Optional.ofNullable(commentRepository.findCommentsByItemId(itemId))
                .orElse(Collections.emptyList())
                .stream()
                .map(commentMapper::toDto)
                .toList();

        return item.getOwner().getId().equals(userId) ? itemMapper.mapToItemDtoAllFields(item, lastBooking(itemId),
                nearestBooking(itemId), commentDtos) : itemMapper.mapToItemDtoAllFields(item, null, null,
                commentDtos);
    }

    @Override
    public List<ResponseItem> getItemsForRent(Long userId, String text) {
        checkUserExists(userId);
        return itemMapper.mapItemToResponseItem(itemRepository.findItemsByText(text));
    }

    @Override
    public ItemDto addItem(ItemCreateDto itemCreateDto) {
        checkUserExists(itemCreateDto.getOwnerId());
        return itemMapper.toDto(itemRepository.save(itemMapper.mapToItem(itemCreateDto)));
    }

    @Override
    public ItemDto updateItem(Long userId, RequestItem requestItem) {
        checkUserExists(userId);
        Item oldItem = itemRepository.findById(requestItem.getId())
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + requestItem.getId()));

        if (!Objects.equals(userId, oldItem.getOwner().getId())) {
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
        return  itemMapper.toDto(itemRepository.save(itemMapper.mapRequestItemToItem(requestItem)));
    }

    private void checkUserExists(Long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User not found with id: " + userId));
    }

    private BookingDateDto lastBooking(Long itemId) {
        Booking lastBooking = bookingRepository.findByItemIdAndEndBeforeOrEqualsOrderByStartDesc(itemId, LocalDateTime.now());
        System.out.println("lastBooking " + lastBooking);
        return lastBooking != null ? bookingMapper.toBookingDateDto(lastBooking) : null;
    }

    private BookingDateDto nearestBooking(Long itemId) {
        Booking nearsetBooking = bookingRepository.getByItemIdAndStartAfterOrderByStartAsc(itemId, LocalDateTime.now());
        System.out.println("nearsetBooking " + nearsetBooking);
        return nearsetBooking != null ? bookingMapper.toBookingDateDto(nearsetBooking) : null;
    }
}
