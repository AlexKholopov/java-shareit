package ru.practicum.shareit.itemRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.itemRequest.dto.ItemRequestInput;
import ru.practicum.shareit.utils.ConstantUtils;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@Valid @RequestBody ItemRequestInput itemRequestInput,
                                                @RequestHeader(ConstantUtils.USER_ID) long user) {
        return itemRequestClient.createRequest(itemRequestInput, user);
    }

    @GetMapping
    public ResponseEntity<Object> findUserRequests(@RequestHeader(ConstantUtils.USER_ID) long user) {
        return itemRequestClient.findUserRequests(user);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@PositiveOrZero @RequestParam(required = false, defaultValue = ConstantUtils.DEFAULT_FROM) int from,
                                                 @PositiveOrZero @RequestParam(required = false, defaultValue = ConstantUtils.DEFAULT_SIZE) int size,
                                                 @RequestHeader(ConstantUtils.USER_ID) long user) {
        return itemRequestClient.findAllRequests(from, size, user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable long id, @RequestHeader(ConstantUtils.USER_ID) long user) {
        return itemRequestClient.getById(id, user);
    }
}