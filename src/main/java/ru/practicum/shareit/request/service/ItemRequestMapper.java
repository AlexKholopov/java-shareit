package ru.practicum.shareit.request.service;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.model.dto.ItemForRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.dto.ItemRequestInput;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ItemRequestMapper {
    @Mapping(target = "created", source = "created")
    @Mapping(target = "owner", source = "user")
    ItemRequest toModel(ItemRequestInput itemRequestInput, LocalDateTime created, User user);

    @Mapping(target = "items", source = "items")
    ItemRequestDto toDto(ItemRequest itemRequest, List<ItemForRequest> items);
}
