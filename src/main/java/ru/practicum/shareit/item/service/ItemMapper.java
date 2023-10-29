package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Slf4j
public class ItemMapper {
    private ItemMapper() {
    }

    public static Item createFromDto(ItemDto itemDto, long owner) {
        Item item = new Item(owner);
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setAvailable(itemDto.getAvailable());
        item.setDescription(itemDto.getDescription());
        return item;
    }

    public static Item updateFromDto(ItemDto itemDto, long owner, Item oldItem) {
        Item item = new Item(owner);
        item.setId(itemDto.getId());
        if (itemDto.getAvailable() == null) {
            item.setAvailable(oldItem.getAvailable());
        } else {
            log.info("Available update requested");
            item.setAvailable(itemDto.getAvailable());
        }
        if (itemDto.getName() == null) {
            item.setName(oldItem.getName());
        } else {
            log.info("Name update requested");
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() == null) {
            item.setDescription(oldItem.getDescription());
        } else {
            log.info("Description update requested");
            item.setDescription(itemDto.getDescription());
        }
        return item;
    }

    public static ItemDto mapToDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        return itemDto;
    }
}
