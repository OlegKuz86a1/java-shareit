package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.practicum.shareit.booking.dto.BookingDateDto;
import ru.practicum.shareit.common.EntityMapper;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public abstract class ItemMapper implements EntityMapper<ItemDto, Item> {

    @Override
    @Mapping(target = "isAvailable", expression = "java(entity.isAvailable())")
    @Mapping(target = "ownerId", source = "entity.owner.id")
    public abstract ItemDto toDto(Item entity);

    @Mapping(target = "owner", expression = "java(ru.practicum.shareit.user.model.User.builder().id(itemCreateDto.getOwnerId()).build())")
    @Mapping(target = "itemRequest", expression = "java(itemCreateDto.getRequestId() != null ? ru.practicum.shareit.request.model.ItemRequest.builder().id(itemCreateDto.getRequestId()).build() : null)")
    public abstract Item mapToItem(ItemCreateDto itemCreateDto);

    public abstract Item mapRequestItemToItem(RequestItem requestItem);

    public abstract ResponseItem mapItemToResponseItem(Item item);

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

//    public ItemDto mapToItemDtoWithOwner(Item item) {
//        return ItemDto.builder()
//                .ownerId(item.getOwner().getId())
//                .requestId(item.getItemRequest().getId())
//                .name(item.getName())
//                .description(item.getDescription())
//                .isAvailable(item.isAvailable())
//                .build();
//    }

}
