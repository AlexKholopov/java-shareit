package ru.practicum.shareit.booking.model.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.model.Status;

import java.time.OffsetDateTime;

@Data
public class BookingDto {
    private long requesterId;
    private long itemId;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private Status status;
}
