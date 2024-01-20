package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class ItemForRequest {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private long requestId;
}