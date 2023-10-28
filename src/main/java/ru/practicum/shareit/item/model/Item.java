package ru.practicum.shareit.item.model;

import lombok.Data;

@Data
public class Item {
    private final long owner;
    private long id;
    private String name;
    private String description;
    private Boolean available;
}
