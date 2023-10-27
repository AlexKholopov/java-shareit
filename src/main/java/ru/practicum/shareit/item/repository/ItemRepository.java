package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item createItem(Item item);

    Item updateItem(Item item);

    void deleteItem(long id);

    Item getItemById(long id);

    List<Item> getItemsByUserId(long owner);

    List<Item> searchItems(String text);
}
