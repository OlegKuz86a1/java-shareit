package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.EntityMapper;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.RequestItem;
import ru.practicum.shareit.item.dto.ResponseItem;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Service
@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public abstract class ItemMapper implements EntityMapper<ItemDto, Item> {

    public abstract Item mapToItem(ItemCreateDto itemCreateDto);

    public abstract Item mapRequestItemToItem(RequestItem requestItem);

    public abstract List<ResponseItem> mapItemToResponseItem(List<Item> item);
}
