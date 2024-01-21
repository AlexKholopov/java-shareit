package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
import ru.practicum.shareit.item.dto.CommentIncome;
import ru.practicum.shareit.item.dto.ItemIncome;
import ru.practicum.shareit.utils.ConstantUtils;
import ru.practicum.shareit.utils.Marker;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestBody @Validated(Marker.OnCreate.class) ItemIncome itemIncome,
                                             @RequestHeader(ConstantUtils.USER_ID) long owner) {
        log.info("Requested creating item");
        return itemClient.createItem(itemIncome, owner);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestBody @Validated(Marker.OnUpdate.class) ItemIncome itemIncome,
                                             @RequestHeader(ConstantUtils.USER_ID) long owner,
                                             @PathVariable long itemId) {
        log.info("Requested item with id {} update", itemIncome.getId());
        if (itemIncome.getId() == 0) {
            log.info("Item assigned an id - {} from the path", itemId);
            itemIncome.setId(itemId);
        }
        return itemClient.updateItem(itemIncome, owner, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getUsersItems(@PositiveOrZero @RequestParam(required = false, defaultValue = ConstantUtils.DEFAULT_FROM) int from,
                                                @PositiveOrZero @RequestParam(required = false, defaultValue = ConstantUtils.DEFAULT_SIZE) int size,
                                                @RequestHeader(ConstantUtils.USER_ID) long owner) {
        log.info("Requested all user {} items", owner);
        return itemClient.getUserItems(from, size, owner);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@PositiveOrZero @RequestParam(required = false, defaultValue = ConstantUtils.DEFAULT_FROM) int from,
                                              @PositiveOrZero @RequestParam(required = false, defaultValue = ConstantUtils.DEFAULT_SIZE) int size,
                                              @RequestParam String text) {
        log.info("Requested items like {}", text.toLowerCase());
        return itemClient.searchItems(from, size, text);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(ConstantUtils.USER_ID) long user,
                                              @PathVariable long itemId) {
        log.info("Requested item with id {}", itemId);
        return itemClient.getItemById(itemId, user);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(ConstantUtils.USER_ID) long user,
                                             @PathVariable long itemId,
                                             @RequestBody @Valid CommentIncome commentIncome) {
        log.info("requested add comment for item {} by user {}", itemId, user);
        return itemClient.addComment(user, itemId, commentIncome);
    }
}
