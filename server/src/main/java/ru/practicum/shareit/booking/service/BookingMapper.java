package ru.practicum.shareit.booking.service;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingIncome;
import ru.practicum.shareit.item.model.dto.ItemShort;
import ru.practicum.shareit.user.model.dto.UserId;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface BookingMapper {

    Booking fromDTO(BookingDto bookingDto);

    BookingDto toDTO(Booking booking);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "item", source = "item")
    @Mapping(target = "booker", source = "user")
    BookingDto fromIncome(BookingIncome bookingIncome, ItemShort item, UserId user, long id);
}
