package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.model.dto.CommentDto;
import ru.practicum.shareit.item.model.dto.CommentIncome;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemIncome;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.utils.Header;
import ru.practicum.shareit.utils.Marker;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @Validated(Marker.OnCreate.class)
    public ItemDto createItem(@RequestBody @Valid ItemIncome itemIncome, @RequestHeader(Header.USER_ID) long owner) {
        log.info("Requested creating item");
        return itemService.createItem(itemIncome, owner);
    }

    @PatchMapping("/{itemId}")
    @Validated(Marker.OnUpdate.class)
    public ItemDto updateItem(@RequestBody @Valid ItemIncome itemIncome,
                              @RequestHeader(Header.USER_ID) long owner,
                              @PathVariable long itemId) {
        log.info("Requested item with id {} update", itemIncome.getId());
        if (itemIncome.getId() == 0) {
            log.info("Item assigned an id - {} from the path", itemId);
            itemIncome.setId(itemId);
        }
        return itemService.updateItem(itemIncome, owner);
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
    public CommentDto addComment(@RequestHeader(Header.USER_ID) long user, @PathVariable long itemId, @RequestBody @Valid CommentIncome commentIncome) {
        log.info("requested add comment for item {} by user {}", itemId, user);
        return itemService.addComment(itemId, user, commentIncome);
    }
}
