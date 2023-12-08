package ru.practicum.shareit.user.service;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserId;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserMapper {
    @Mapping(source = "user.id", target = "id")
    UserId toId(User user);
}
