package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.control.DeepClone;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.common.EntityMapper;

@Service
@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE, mappingControl = DeepClone.class)
public abstract class BookingMapper implements EntityMapper<BookingDto, Booking> {

    @Mapping(target = "item", expression = "java(ru.practicum.shareit.item.model.Item.builder().id(createDto.getItemId()).build())")
    @Mapping(target = "booker", expression = "java(ru.practicum.shareit.user.model.User.builder().id(createDto.getBookerId()).build())")
    public abstract Booking toEntity(BookingCreateDto createDto);

    public abstract BookingDateDto toBookingDateDto(Booking booking);

}
