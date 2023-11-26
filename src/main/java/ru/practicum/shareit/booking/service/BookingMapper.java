package ru.practicum.shareit.booking.service;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingIncome;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface BookingMapper {

    Booking fromDTO(BookingDto bookingDto);

    BookingDto toDTO(Booking booking);

    @Mapping(target = "id", source = "bookingIncome.id")
    @Mapping(target = "item", source = "item")
    @Mapping(target = "booker", source = "user")
    BookingDto fromIncome(BookingIncome bookingIncome, Item item, User user);
}
