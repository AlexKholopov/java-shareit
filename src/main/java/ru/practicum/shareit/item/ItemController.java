package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.headers.Header;
import ru.practicum.shareit.item.model.dto.CommentDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader(Header.USER_ID) long owner) {
        log.info("Requested creating item");
        return itemService.createItem(itemDto, owner);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @RequestHeader(Header.USER_ID) long owner,
                              @PathVariable long itemId) {
        log.info("Requested item with id {} update", itemDto.getId());
        if (itemDto.getId() == 0) {
            log.info("Item assigned an id - {} from the path", itemId);
            itemDto.setId(itemId);
        }
        return itemService.updateItem(itemDto, owner);
    }

    @GetMapping
    public List<ItemDto> getUsersItems(@RequestHeader(Header.USER_ID) long owner) {
        log.info("Requested all user {} items", owner);
        return itemService.getUserItems(owner);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.info("Requested items like {}", text.toLowerCase());
        return itemService.searchItems(text);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader(Header.USER_ID) long user, @PathVariable long itemId) {
        log.info("Requested item with id {}", itemId);
        return itemService.getItemById(itemId, user);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(Header.USER_ID) long user, @PathVariable long itemId, @RequestBody @Valid CommentDto commentDto) {
        log.info("requested add comment for item {} by user {}", itemId, user);
        return itemService.addComment(itemId, user, commentDto);
    }
}
