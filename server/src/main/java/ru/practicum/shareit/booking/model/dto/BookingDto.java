package ru.practicum.shareit.booking.model.dto;

import lombok.Data;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.dto.ItemShort;
import ru.practicum.shareit.user.model.dto.UserId;

import java.time.LocalDateTime;

@Data
public class BookingDto {
    private long id;
    private UserId booker;
    private ItemShort item;
    private LocalDateTime start;
    private LocalDateTime end;
    private Status status;
}
