package ru.practicum.shareit.item.model.dto;

import lombok.Data;
import ru.practicum.shareit.booking.model.dto.BookingForItem;
import ru.practicum.shareit.utils.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.List;

@Data
public class ItemDto {
    @Null(groups = Marker.OnCreate.class)
    @NotNull(groups = Marker.OnUpdate.class)
    private long id;
    @NotBlank(groups = Marker.OnCreate.class)
    @NotEmpty(groups = Marker.OnCreate.class)
    private String name;
    @NotBlank(groups = Marker.OnCreate.class)
    @NotEmpty(groups = Marker.OnCreate.class)
    private String description;
    @NotNull
    private Boolean available;
    private BookingForItem lastBooking;
    private BookingForItem nextBooking;
    private List<CommentDto> comments;
}
