package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.dto.ItemRequestInput;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemMapper itemMapper;

    public ItemRequestDto createRequest(ItemRequestInput itemRequestInput, long user) {
        User userFromDb = userRepository.findById(user).orElseThrow(() -> new NotFoundException("No such user was found"));
        LocalDateTime created = LocalDateTime.now();
        return itemRequestMapper.toDto(itemRequestRepository.save(itemRequestMapper.toModel(itemRequestInput, created, userFromDb)), Collections.emptyList());
    }

    public List<ItemRequestDto> findUserRequests(long user) {
        User userFromDb = userRepository.findById(user).orElseThrow(() -> new NotFoundException("No such user was found"));
        List<ItemRequest> requests = itemRequestRepository.findByOwner(userFromDb);
        var items = itemRepository.findByRequestIn(requests);
        if (items.isEmpty()) {
            return requests.stream().map(it -> itemRequestMapper.toDto(it, Collections.emptyList())).collect(Collectors.toList());
        }
        Map<Long, List<Item>> itemsGroupedByRequestId = items.stream().collect(Collectors.groupingBy(it -> it.getRequest().getId()));
        return requests.stream()
                .map(it -> itemRequestMapper.toDto(it, itemsGroupedByRequestId.get(it.getId()).stream()
                        .map(itemMapper::toRequest).collect(Collectors.toList())))
                .sorted(Comparator.comparing(ItemRequestDto::getCreated))
                .collect(Collectors.toList());
    }

    public List<ItemRequestDto> findAllRequests(int from, int size, long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("No such user was found"));
        PageRequest pageRequest = PageRequest.of(from, size, Sort.Direction.DESC, "created");
        var res = itemRequestRepository.findByOwnerNot(user, pageRequest);
        var items = itemRepository.findByRequestIn(res);
        if (items.isEmpty()) {
            return res.stream().map(it -> itemRequestMapper.toDto(it, Collections.emptyList())).collect(Collectors.toList());
        }
        Map<Long, List<Item>> itemsGroupedByRequestId = items.stream().collect(Collectors.groupingBy(it -> it.getRequest().getId()));

        return res.stream()
                .map(it -> itemRequestMapper.toDto(it, itemsGroupedByRequestId.get(it.getId()).stream()
                        .map(itemMapper::toRequest).collect(Collectors.toList())))
                .sorted(Comparator.comparing(ItemRequestDto::getCreated))
                .collect(Collectors.toList());
    }

    public ItemRequestDto getById(long userId, long requestId) {
        if (!userRepository.existsById(userId)) throw new NotFoundException("No such user was found");
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("No such item request was found"));
        var items = itemRepository.findByRequestIn(List.of(itemRequest)).stream().map(itemMapper::toRequest).collect(Collectors.toList());
        return itemRequestMapper.toDto(itemRequest, items);
    }
}
