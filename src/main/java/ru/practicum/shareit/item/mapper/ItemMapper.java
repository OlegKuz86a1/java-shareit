package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDateDto;
import ru.practicum.shareit.common.EntityMapper;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Service
@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public abstract class ItemMapper implements EntityMapper<ItemDto, Item> {

    @Mapping(target = "owner", expression = "java(ru.practicum.shareit.user.model.User.builder().id(itemCreateDto.getOwnerId()).build())")
    public abstract Item mapToItem(ItemCreateDto itemCreateDto);

    public abstract Item mapRequestItemToItem(RequestItem requestItem);

    public abstract List<ResponseItem> mapItemToResponseItem(List<Item> item);

    public ItemDtoAllFields mapToItemDtoAllFields(Item item, BookingDateDto last, BookingDateDto nearest, List<CommentDto> commentDtos) {
        return ItemDtoAllFields.builder()
                .id(item.getId())
                .ownerId(item.getOwner().getId())
                .name(item.getName())
                .description(item.getDescription())
                .isAvailable(item.isAvailable())
                .lastBooking(last)
                .nearestBooking(nearest)
                .comments(commentDtos)
                .build();
    }

    public ItemDtoAllFields mapToItemDtoWithDateBooking(Item item, BookingDateDto last, BookingDateDto nearest) {
        return ItemDtoAllFields.builder()
                .id(item.getId())
                .ownerId(item.getOwner().getId())
                .name(item.getName())
                .description(item.getDescription())
                .isAvailable(item.isAvailable())
                .lastBooking(last)
                .nearestBooking(nearest)
                .build();
    }

}
