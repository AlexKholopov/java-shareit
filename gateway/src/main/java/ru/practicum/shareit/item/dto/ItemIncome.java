package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.utils.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ItemIncome {
    private long id;
    @NotBlank(groups = Marker.OnCreate.class)
    @Size(max = 40, groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    private String name;
    @NotBlank(groups = Marker.OnCreate.class)
    @Size(max = 200, groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    private String description;
    @NotNull(groups = Marker.OnCreate.class)
    private Boolean available;
    private Long requestId;
}
