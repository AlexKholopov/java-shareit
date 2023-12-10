package ru.practicum.shareit.item.service;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.CommentDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemIncome;
import ru.practicum.shareit.item.model.dto.ItemShort;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ItemMapper {

    ItemDto toDTO(Item item, List<CommentDto> comments);

    @Mapping(target = "owner", source = "owner")
    @Mapping(target = "id", source = "itemIncome.id")
    @Mapping(target = "name", source = "itemIncome.name")
    Item toModel(ItemIncome itemIncome, User owner);

    @Mapping(target = "id", source = "item.id")
    @Mapping(target = "name", source = "item.name")
    ItemShort toShort(Item item);
}
