package ru.practicum.shareit.item.service;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ItemMapper {

    ItemDto toDTO(Item item);

    @Mapping(target = "owner", source = "owner")
    Item toModel(ItemDto itemDto, long owner);
}
