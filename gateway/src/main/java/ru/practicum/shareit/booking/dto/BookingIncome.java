package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.utils.Marker;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookingIncome {
    private long itemId;
    @FutureOrPresent(groups = Marker.OnCreate.class)
    @NotNull(groups = Marker.OnCreate.class)
    private LocalDateTime start;
    @Future(groups = Marker.OnCreate.class)
    @NotNull(groups = Marker.OnCreate.class)
    private LocalDateTime end;
}
