package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.dto.ItemRequestInput;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.utils.ConstantUtils;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService service;

    @PostMapping
    public ItemRequestDto createRequest(@RequestBody ItemRequestInput itemRequestInput,
                                        @RequestHeader(ConstantUtils.USER_ID) long user) {
        return service.createRequest(itemRequestInput, user);
    }

    @GetMapping
    public List<ItemRequestDto> findRequestsById(@RequestHeader(ConstantUtils.USER_ID) long user) {
        return service.findUserRequests(user);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestParam(required = false, defaultValue = ConstantUtils.DEFAULT_FROM) int from,
                                               @RequestParam(required = false, defaultValue = ConstantUtils.DEFAULT_SIZE) int size,
                                               @RequestHeader(ConstantUtils.USER_ID) long user) {
        return service.findAllRequests(from, size, user);
    }

    @GetMapping("/{id}")
    public ItemRequestDto getById(@PathVariable long id,
                                  @RequestHeader(ConstantUtils.USER_ID) long user) {
        return service.getById(user, id);
    }
}
