package ru.practicum.shareit.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.control.DeepClone;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.EntityMapper;
import ru.practicum.shareit.user.dto.UserBaseGetter;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;

@Service
@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE, mappingControl = DeepClone.class)
public abstract class UserMapper implements EntityMapper<UserDto, User> {

    public abstract User toEntity(UserBaseGetter user);

}
