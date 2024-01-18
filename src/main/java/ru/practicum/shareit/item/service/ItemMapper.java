package ru.practicum.shareit.item.service;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.CommentDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemForRequest;
import ru.practicum.shareit.item.model.dto.ItemIncome;
import ru.practicum.shareit.item.model.dto.ItemShort;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ItemMapper {

    @Mapping(target = "requestId", source = "item.request.id")
    ItemDto toDTO(Item item, List<CommentDto> comments);

    @Mapping(target = "owner", source = "owner")
    @Mapping(target = "id", source = "itemIncome.id")
    @Mapping(target = "name", source = "itemIncome.name")
    @Mapping(target = "request", source = "itemRequest")
    @Mapping(target = "description", source = "itemIncome.description")
    Item toModel(ItemIncome itemIncome, User owner, ItemRequest itemRequest);

    @Mapping(target = "owner", source = "owner")
    @Mapping(target = "id", source = "itemIncome.id")
    @Mapping(target = "name", source = "itemIncome.name")
    @Mapping(target = "description", source = "itemIncome.description")
    Item toModel(ItemIncome itemIncome, User owner);

    @Mapping(target = "id", source = "item.id")
    @Mapping(target = "name", source = "item.name")
    ItemShort toShort(Item item);

    @Mapping(target = "requestId", source = "item.request.id")
    ItemForRequest toRequest(Item item);
}
