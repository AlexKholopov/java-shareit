package ru.practicum.shareit.booking.model.dto;

import lombok.Data;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.dto.ItemShort;
import ru.practicum.shareit.user.model.UserId;
import ru.practicum.shareit.utils.Marker;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class BookingDto {
    private long id;
    private UserId booker;
    private ItemShort item;
    @FutureOrPresent(groups = Marker.OnCreate.class)
    @NotNull(groups = Marker.OnCreate.class)
    private LocalDateTime start;
    @Future(groups = Marker.OnCreate.class)
    @NotNull(groups = Marker.OnCreate.class)
    private LocalDateTime end;
    private Status status;
}
