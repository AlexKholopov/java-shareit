package ru.practicum.shareit.item.service;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.CommentDto;
import ru.practicum.shareit.item.model.dto.CommentIncome;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CommentMapper {
    @Mapping(source = "comment.user.name", target = "authorName")
    CommentDto toDTO(Comment comment);

    @Mapping(target = "user", source = "user")
    @Mapping(target = "id", source = "commentDto.id")
    @Mapping(target = "item", source = "item")
    Comment fromDTO(CommentDto commentDto, User user, Item item);

    @Mapping(target = "user", source = "user")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "item", source = "item")
    Comment fromIncome(CommentIncome commentIncome, User user, Item item, Long id);
}
