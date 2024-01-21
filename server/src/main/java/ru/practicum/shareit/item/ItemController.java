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
import ru.practicum.shareit.item.model.dto.CommentDto;
import ru.practicum.shareit.item.model.dto.CommentIncome;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemIncome;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.utils.ConstantUtils;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestBody ItemIncome itemIncome,
                              @RequestHeader(ConstantUtils.USER_ID) long owner) {
        log.info("Requested creating item");
        return itemService.createItem(itemIncome, owner);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemIncome itemIncome,
                              @RequestHeader(ConstantUtils.USER_ID) long owner,
                              @PathVariable long itemId) {
        log.info("Requested item with id {} update", itemIncome.getId());
        if (itemIncome.getId() == 0) {
            log.info("Item assigned an id - {} from the path", itemId);
            itemIncome.setId(itemId);
        }
        return itemService.updateItem(itemIncome, owner);
    }

    @GetMapping
    public List<ItemDto> getUsersItems(@RequestParam(required = false, defaultValue = ConstantUtils.DEFAULT_FROM) int from,
                                       @RequestParam(required = false, defaultValue = ConstantUtils.DEFAULT_SIZE) int size,
                                       @RequestHeader(ConstantUtils.USER_ID) long owner) {
        log.info("Requested all user {} items", owner);
        return itemService.getUserItems(from, size, owner);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam(required = false, defaultValue = ConstantUtils.DEFAULT_FROM) int from,
                                     @RequestParam(required = false, defaultValue = ConstantUtils.DEFAULT_SIZE) int size,
                                     @RequestParam String text) {
        log.info("Requested items like {}", text.toLowerCase());
        return itemService.searchItems(from, size, text);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader(ConstantUtils.USER_ID) long user,
                               @PathVariable long itemId) {
        log.info("Requested item with id {}", itemId);
        return itemService.getItemById(itemId, user);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(ConstantUtils.USER_ID) long user,
                                 @PathVariable long itemId,
                                 @RequestBody CommentIncome commentIncome) {
        log.info("requested add comment for item {} by user {}", itemId, user);
        return itemService.addComment(itemId, user, commentIncome);
    }
}
