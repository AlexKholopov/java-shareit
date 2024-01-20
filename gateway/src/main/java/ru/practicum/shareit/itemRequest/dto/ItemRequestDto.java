package ru.practicum.shareit.itemRequest.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemForRequest;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemRequestDto {
    private long id;
    private String description;
    private LocalDateTime created;
    private List<ItemForRequest> items;
}