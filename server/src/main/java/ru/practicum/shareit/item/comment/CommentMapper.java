package ru.practicum.shareit.item.comment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.practicum.shareit.common.EntityMapper;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.user.mapper.UserMapper;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE, uses = UserMapper.class)
public abstract class CommentMapper implements EntityMapper<CommentDto, Comment> {

    @Mapping(target = "authorName", expression = "java(comment.getAuthor().getName())")
    public abstract CommentDto toDto(Comment comment);

}
