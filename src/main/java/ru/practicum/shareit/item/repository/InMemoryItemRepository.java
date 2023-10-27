package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class InMemoryItemRepository implements ItemRepository {
    private long idCount = 1;
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item createItem(Item item) {
        item.setId(idCount++);
        items.put(item.getId(), item);
        log.info("Item successfully created");
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        if (!items.containsKey(item.getId())) {
            log.error("Updating item fail, item id {} not found", item.getId());
            throw new NotFoundException(String.format("Item with id %d not found", item.getId()));
        }
        Item oldItem = getItemById(item.getId());
        if (item.getAvailable() == null) {
            item.setAvailable(oldItem.getAvailable());
        } else {
            log.info("Available update requested");
        }
        if (item.getName() == null) {
            item.setName(oldItem.getName());
        } else {
            log.info("Name update requested");
        }
        if (item.getDescription() == null) {
            item.setDescription(oldItem.getDescription());
        } else {
            log.info("Description update requested");
        }
        items.replace(item.getId(), item);
        log.info("Item successfully updated");
        return item;
    }

    @Override
    public void deleteItem(long id) {
        items.remove(id);
        log.info("Item {} successfully deleted", id);
    }

    @Override
    public Item getItemById(long id) {
        if (!items.containsKey(id)) {
            log.error("Item with id {} not found", id);
            throw new NotFoundException(String.format("Item with id %s not found", id));
        }
        log.info("Successful item id {} request", id);
        return items.get(id);
    }

    @Override
    public List<Item> getItemsByUserId(long owner) {
        log.info("Successful items by user id {} request", owner);
        return items.values().stream().filter(i -> i.getOwner() == owner).collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItems(String text) {
        log.info("Successful items like {} request", text.toLowerCase());
        return items.values().stream().filter(i -> i.getName().toLowerCase().contains(text.toLowerCase()) && i.getAvailable() ||
                i.getDescription().toLowerCase().contains(text.toLowerCase()) && i.getAvailable()).collect(Collectors.toList());
    }
}
