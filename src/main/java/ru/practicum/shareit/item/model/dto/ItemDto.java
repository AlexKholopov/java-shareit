package ru.practicum.shareit.item.model.dto;

import lombok.Data;
import ru.practicum.shareit.booking.model.dto.BookingForItem;

import java.util.List;

@Data
public class ItemDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingForItem lastBooking;
    private BookingForItem nextBooking;
    private List<CommentDto> comments;
    private long requestId;
}
