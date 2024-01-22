package ru.practicum.shareit.item.model.dto;

import lombok.Data;

@Data
public class ItemIncome {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
}
