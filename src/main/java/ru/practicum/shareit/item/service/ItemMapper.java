package ru.practicum.shareit.item.service;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.CommentDto;
import ru.practicum.shareit.item.model.dto.ItemDto;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ItemMapper {

    ItemDto toDTO(Item item, List<CommentDto> comments);

    @Mapping(target = "owner", source = "owner")
    Item toModel(ItemDto itemDto, long owner);
}
