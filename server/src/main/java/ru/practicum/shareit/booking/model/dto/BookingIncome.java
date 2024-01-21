package ru.practicum.shareit.booking.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingIncome {
    private long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}
