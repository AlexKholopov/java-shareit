package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingForItem;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingMapper;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.LockedException;
import ru.practicum.shareit.exceptions.NoAuthorizationException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.CommentDto;
import ru.practicum.shareit.item.model.dto.CommentIncome;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemIncome;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final ItemRequestRepository itemRequestRepository;

    public ItemDto createItem(ItemIncome itemIncome, long owner) {
        User user = userRepository.findById(owner).orElseThrow(() -> new NotFoundException("No such owner was found"));
        Item item;
        if (itemIncome.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(itemIncome.getRequestId()).orElseThrow(() -> new NotFoundException("No such request was found"));
            item = itemRepository.save(itemMapper.toModel(itemIncome, user, itemRequest));
        } else {
            item = itemRepository.save(itemMapper.toModel(itemIncome, user));
        }
        return itemMapper.toDTO(item, Collections.emptyList());
    }

    public ItemDto updateItem(ItemIncome itemIncome, long owner) {
        Item item = itemRepository.findById(itemIncome.getId()).orElseThrow(() -> new NotFoundException("No such item was found"));
        if (item.getOwner().getId() != owner) {
            log.error("Unauthorized update attempt");
            throw new NoAuthorizationException("You do not have authorization to update the object");
        }
        itemIncome.setName(itemIncome.getName() == null ? item.getName() : itemIncome.getName());
        itemIncome.setDescription(itemIncome.getDescription() == null ? item.getDescription() : itemIncome.getDescription());
        itemIncome.setAvailable(itemIncome.getAvailable() == null ? item.getAvailable() : itemIncome.getAvailable());
        Item item1 = itemRepository.save(itemMapper.toModel(itemIncome, item.getOwner()));
        var comments = commentRepository.findByItem(item1).stream().map(commentMapper::toDTO).collect(toList());
        return itemMapper.toDTO(item1, comments);
    }

    public List<ItemDto> getUserItems(int from, int size, long owner) {
        User user = userRepository.findById(owner).orElseThrow(() -> new ConflictException("No such user was found"));
        PageRequest pageRequest = PageRequest.of(from, size);
        var items = itemRepository.findByOwner(user, pageRequest);

        Map<Long, List<BookingDto>> map = bookingRepository.findByItemInAndStartLessThanEqualAndStatus(items, LocalDateTime.now(), Status.APPROVED).stream()
                .map(bookingMapper::toDTO)
                .sorted(Comparator.comparing(it -> -it.getStart().toInstant(ZoneOffset.UTC).toEpochMilli()))
                .collect(Collectors.groupingBy(it -> it.getItem().getId(), toList()));
        Map<Long, List<BookingDto>> map2 = bookingRepository.findByItemInAndStartAfterAndStatus(items, LocalDateTime.now(), Status.APPROVED).stream()
                .map(bookingMapper::toDTO)
                .sorted(Comparator.comparing(it -> it.getStart().toInstant(ZoneOffset.UTC).toEpochMilli()))
                .collect(Collectors.groupingBy(it -> it.getItem().getId(), toList()));

        var itemsDto = items.stream().map((it) -> {
            var comments = commentRepository.findByItem(it).stream().map(commentMapper::toDTO).collect(toList());
            return itemMapper.toDTO(it, comments);
        }).sorted(Comparator.comparingLong(ItemDto::getId)).collect(toList());
        itemsDto.forEach(it -> {
            if (!map.isEmpty() && map.get(it.getId()) != null) {
                BookingDto bookingDto = map.get(it.getId()).get(0);
                if (bookingDto == null) {
                    it.setLastBooking(null);
                } else {
                    BookingForItem booking = new BookingForItem(bookingDto.getId(), bookingDto.getBooker().getId());
                    it.setLastBooking(booking);
                }
            }
            if (!map2.isEmpty() && map.get(it.getId()) != null) {
                BookingDto bookingDto = map2.get(it.getId()).get(0);
                if (bookingDto == null) {
                    it.setNextBooking(null);
                } else {
                    BookingForItem booking = new BookingForItem(bookingDto.getId(), bookingDto.getBooker().getId());
                    it.setNextBooking(booking);
                }
            }
        });

        return itemsDto;
    }

    public List<ItemDto> searchItems(int from, int size, String text) {
        if (text.isBlank()) {
            log.info("Empty search request");
            return Collections.emptyList();
        }
        PageRequest pageRequest = PageRequest.of(from, size);
        var items = itemRepository.findByText(text, pageRequest);
        Map<Long, List<CommentDto>> comments = commentRepository.findByItemIn(items)
                .stream()
                .sorted(Comparator.comparingLong(it -> it.getCreated().toInstant(ZoneOffset.UTC).toEpochMilli()))
                .map(commentMapper::toDTO)
                .collect(Collectors.groupingBy(CommentDto::getId, toList()));
        List<ItemDto> result = new ArrayList<>();
        items.forEach(item -> result.add(itemMapper.toDTO(item, comments.get(item.getId()))));
        return result;
    }

    public ItemDto getItemById(long id, long userId) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new NotFoundException("No such item was found"));
        var comments = commentRepository.findByItem(item).stream().map(commentMapper::toDTO).collect(toList());
        ItemDto itemDto = itemMapper.toDTO(item, comments);
        if (item.getOwner().getId() == userId) {
            List<BookingDto> list = bookingRepository.findByItemAndStartLessThanEqualAndStatus(item, LocalDateTime.now(), Status.APPROVED).stream()
                    .map(bookingMapper::toDTO)
                    .sorted(Comparator.comparing(it -> -it.getStart().toInstant(ZoneOffset.UTC).toEpochMilli()))
                    .collect(toList());
            List<BookingDto> list2 = bookingRepository.findByItemAndStartAfterAndStatus(item, LocalDateTime.now(), Status.APPROVED).stream()
                    .map(bookingMapper::toDTO)
                    .sorted(Comparator.comparing(it -> it.getStart().toInstant(ZoneOffset.UTC).toEpochMilli()))
                    .collect(toList());
            if (!list.isEmpty()) {
                BookingForItem booking = new BookingForItem(list.get(0).getId(), list.get(0).getBooker().getId());
                itemDto.setLastBooking(booking);
            }
            if (!list2.isEmpty()) {
                BookingForItem booking = new BookingForItem(list2.get(0).getId(), list2.get(0).getBooker().getId());
                itemDto.setNextBooking(booking);
            }
        }
        return itemDto;
    }

    public CommentDto addComment(long itemId, long userId, CommentIncome commentIncome) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("No such item was found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("No such user was found"));
        boolean exists = bookingRepository.existsAcceptedByBookerAndItemAndTime(user, item, LocalDateTime.now());
        if (!exists) {
            throw new LockedException("No bookings by that user was found");
        }
        if (item.getOwner().getId() == userId) {
            throw new LockedException("You can't comment your item");
        }
        Comment comment = commentMapper.fromIncome(commentIncome, user, item, null);
        comment.setCreated(LocalDateTime.now());
        comment = commentRepository.save(comment);
        return commentMapper.toDTO(comment);
    }
}
