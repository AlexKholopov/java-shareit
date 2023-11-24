package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NoAuthorizationException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    public ItemDto createItem(ItemDto itemDto, long owner) {
        if (userRepository.findById(owner).isEmpty()) {
            throw new NotFoundException("No such owner was found");
        }
        Item item = itemRepository.save(itemMapper.toModel(itemDto, owner));
        return itemMapper.toDTO(item);
    }

    public ItemDto updateItem(ItemDto itemDto, long owner) {
        Optional<Item> maybeItem = itemRepository.findById(itemDto.getId());
        if (maybeItem.isEmpty()) {
            throw new NotFoundException("No such item was found");
        }
        Item item = maybeItem.get();
        if (item.getOwner() != owner) {
            log.error("Unauthorized update attempt");
            throw new NoAuthorizationException("You do not have authorization to update the object");
        }
        itemDto.setName(itemDto.getName() == null ? item.getName() : itemDto.getName());
        itemDto.setDescription(itemDto.getDescription() == null ? item.getDescription() : itemDto.getDescription());
        itemDto.setAvailable(itemDto.getAvailable() == null ? item.getAvailable() : itemDto.getAvailable());
        Item item1 = itemRepository.save(itemMapper.toModel(itemDto, owner));
        return itemMapper.toDTO(item1);
    }

    public List<ItemDto> getUserItems(long owner) {
        return itemRepository.findByOwner(owner).stream()
                .map(itemMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<ItemDto> searchItems(String text) {
        if (text.isBlank() || text.isEmpty()) {
            log.info("Empty search request");
            return Collections.emptyList();
        }
        return itemRepository.findByAvailableTrueAndNameContainingIgnoreCaseOrAvailableTrueAndDescriptionContainingIgnoreCase(text, text).stream()
                .map(itemMapper::toDTO)
                .collect(Collectors.toList());
    }

    public ItemDto getItemById(long id) {
        Optional<Item> maybeItem = itemRepository.findById(id);
        if (maybeItem.isEmpty()) {
            throw new NotFoundException("No such item was found");
        }
        Item item = maybeItem.get();
        return itemMapper.toDTO(item);
    }
}
