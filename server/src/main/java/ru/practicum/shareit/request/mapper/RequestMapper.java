package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.EntityMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestCreate;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE, uses = ItemMapper.class)
public abstract class RequestMapper implements EntityMapper<ItemRequestDto, ItemRequest> {

    @Override
    @Mapping(target = "requestor", expression = "java(ru.practicum.shareit.user.model.User.builder().id(dto.getRequestor()).build())")
    public abstract ItemRequest toEntity(ItemRequestDto dto);

    @Override
    @Mapping(target = "requestor", expression = "java(entity.getRequestor().getId())")
    public abstract ItemRequestDto toDto(ItemRequest entity);

    public ItemRequest mapRequestCreateToEntity(User user, ItemRequestCreate itemRequestCreate) {
       return ItemRequest.builder()
                .description(itemRequestCreate.getDescription())
                .requestor(user)
                .dateRequestCreated(itemRequestCreate.getDateRequestCreated())
                .build();
    }

    @Mapping(target = "requestor", expression = "java(itemRequest.getRequestor().getId())")
    public abstract ItemRequestDtoWithItems mapToRequestWithItem(ItemRequest itemRequest, List<Item> items);

}
