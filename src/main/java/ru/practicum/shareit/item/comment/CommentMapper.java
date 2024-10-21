package ru.practicum.shareit.item.comment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.EntityMapper;
import ru.practicum.shareit.item.comment.dto.CommentDto;

@Service
@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public abstract class CommentMapper implements EntityMapper<CommentDto, Comment> {

    @Mapping(target = "authorName", expression = "java(comment.getAuthor().getName())")
    public abstract CommentDto toDto(Comment comment);

}
