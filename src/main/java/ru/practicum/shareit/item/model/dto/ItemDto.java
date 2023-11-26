package ru.practicum.shareit.item.model.dto;

import lombok.Data;
import ru.practicum.shareit.booking.model.dto.BookingForItem;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ItemDto {
    private long id;
    @NotBlank
    @NotEmpty
    private String name;
    @NotBlank
    @NotEmpty
    private String description;
    @NotNull
    private Boolean available;
    private BookingForItem lastBooking;
    private BookingForItem nextBooking;
    private List<CommentDto> comments;
}
