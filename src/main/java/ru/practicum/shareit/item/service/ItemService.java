package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingForItem;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingMapper;
import ru.practicum.shareit.exceptions.LockedException;
import ru.practicum.shareit.exceptions.NoAuthorizationException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.CommentDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        var items = itemRepository.findByOwner(owner).stream().map(itemMapper::toDTO).sorted(Comparator.comparingLong(ItemDto::getId)).collect(Collectors.toList());
        items.forEach(it -> {
            var bookings = findBookings(it, owner);
            it.setLastBooking(bookings.get(0));
            it.setNextBooking(bookings.get(1));
        });
        return items;
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

    public ItemDto getItemById(long id, long userId) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new NotFoundException("No such item was found"));
        ItemDto itemDto = itemMapper.toDTO(item);
        if (item.getOwner() == userId) {
            var bookings = findBookings(itemDto, userId);
            itemDto.setLastBooking(bookings.get(0));
            itemDto.setNextBooking(bookings.get(1));
        }
        return itemDto;
    }

    public CommentDto addComment(long itemId, long userId, CommentDto commentDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("No such item was found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("No such user was found"));
        List<Booking> bookings = bookingRepository.findAcceptedByBookerAndItem(user, item).stream()
                .filter(it -> it.getStart().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());
        if (bookings.isEmpty()) {
            throw new LockedException("No bookings by that user was found");
        }
        commentDto.setCreated(LocalDateTime.now());
        Comment comment = commentMapper.fromDTO(commentDto, user);
        commentRepository.save(comment);
        return commentMapper.toDTO(comment);
    }

    private List<BookingForItem> findBookings(ItemDto it, long owner) {
        BookingDto lastBooking = bookingRepository.findByItem(itemMapper.toModel(it, owner)).stream()
                .sorted(Comparator.comparingLong(booking -> -booking.getEnd().toInstant(ZoneOffset.UTC).toEpochMilli()))
                .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                .filter(booking -> booking.getStatus().equals(Status.APPROVED))
                .map(bookingMapper::toDTO)
                .findFirst().orElse(null);
        BookingDto nextBooking = bookingRepository.findByItem(itemMapper.toModel(it, owner)).stream()
                .sorted(Comparator.comparingLong(booking -> booking.getStart().toInstant(ZoneOffset.UTC).toEpochMilli()))
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                .filter(booking -> booking.getStatus().equals(Status.APPROVED))
                .map(bookingMapper::toDTO)
                .findFirst().orElse(null);
        BookingForItem last = lastBooking == null ? null : new BookingForItem(lastBooking.getId(), lastBooking.getBooker().getId());
        BookingForItem next = nextBooking == null ? null : new BookingForItem(nextBooking.getId(), nextBooking.getBooker().getId());
        var bookings = new ArrayList<BookingForItem>();
        bookings.add(last);
        bookings.add(next);
        return bookings;
    }
}
