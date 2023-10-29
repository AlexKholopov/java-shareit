package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NoAuthorizationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemDto createItem(ItemDto itemDto, long owner) {
        userRepository.getUserById(owner);
        Item item = itemRepository.createItem(ItemMapper.createFromDto(itemDto, owner));
        return ItemMapper.mapToDto(item);
    }

    public ItemDto updateItem(ItemDto itemDto, long owner) {
        Item item = itemRepository.getItemById(itemDto.getId());
        if (item.getOwner() != owner) {
            log.error("Unauthorized update attempt");
            throw new NoAuthorizationException("You do not have authorization to update the object");
        }
        Item item1 = itemRepository.updateItem(ItemMapper.updateFromDto(itemDto, owner, item));
        return ItemMapper.mapToDto(item1);
    }

    public List<ItemDto> getUserItems(long owner) {
        return itemRepository.getItemsByUserId(owner).stream()
                .map(ItemMapper::mapToDto)
                .collect(Collectors.toList());
    }

    public List<ItemDto> searchItems(String text) {
        if (text.isBlank() || text.isEmpty()) {
            log.info("Empty search request");
            return Collections.emptyList();
        }
        return itemRepository.searchItems(text).stream()
                .map(ItemMapper::mapToDto)
                .collect(Collectors.toList());
    }

    public ItemDto getItemById(long id) {
        return ItemMapper.mapToDto(itemRepository.getItemById(id));
    }
}
