package ru.practicum.shareit.booking.service;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.dto.BookingDto;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface BookingMapper {
    Booking fromDTO(BookingDto bookingDto);

    BookingDto toDTO(Booking booking);
}
