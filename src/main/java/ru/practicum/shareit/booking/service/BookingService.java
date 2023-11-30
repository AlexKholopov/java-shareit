package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingIncome;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.LockedException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public BookingDto addBooking(BookingIncome bookingIncome, long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("No such user was found"));
        User booker = new User();
        booker.setId(userId);
        checkValidBookingTime(bookingIncome);
        Item item = itemRepository.findById(bookingIncome.getItemId()).orElseThrow(() -> new NotFoundException("No such item was found"));
        if (item.getOwner() == userId) {
            throw new NotFoundException("You can't book your item");
        }
        if (!item.getAvailable()) {
            throw new LockedException("Item not available");
        }
        BookingDto bookingDto = bookingMapper.fromIncome(bookingIncome, item, booker);
        bookingDto.setStatus(Status.WAITING);
        Booking booking = bookingMapper.fromDTO(bookingDto);
        bookingRepository.save(booking);
        return bookingMapper.toDTO(booking);
    }

    public BookingDto approveBooking(long bookingId, String approved, long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("No such booking was found"));
        Item item = itemRepository.findById(booking.getItem().getId()).orElseThrow(() -> new NotFoundException("No such item was found"));
        if (item.getOwner() != userId) {
            throw new NotFoundException("Request denied, you don't have access rights");
        }
        if (booking.getStatus() != Status.WAITING) {
            throw new LockedException("You cant change status twice");
        }
        Status status;
        switch (approved) {
            case "true":
                status = Status.APPROVED;
                break;
            case "false":
                status = Status.REJECTED;
                break;
            default:
                throw new LockedException("Bad request");
        }
        booking.setStatus(status);
        bookingRepository.save(booking);
        return bookingMapper.toDTO(booking);
    }

    public BookingDto getBooking(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("No such booking was found"));
        Item item = itemRepository.findById(booking.getItem().getId()).orElseThrow(() -> new NotFoundException("No such item was found"));
        if (booking.getBooker().getId() == userId || item.getOwner() == userId) {
            return bookingMapper.toDTO(booking);
        } else {
            throw new NotFoundException("Request denied, you don't have access rights");
        }
    }

    public List<BookingDto> getAllUserBookings(long userId, String state) {
        User booker = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("No such user was found"));
        var stream = bookingRepository.findByBooker(booker).stream();
        return filterByState(stream, state);
    }

    public List<BookingDto> getAllUsersItemsBookings(long ownerId, String state) {
        userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("No such user was found"));
        var stream = bookingRepository.findBookingsByItemOwner(ownerId).stream();
        return filterByState(stream, state);
    }

    private List<BookingDto> filterByState(Stream<Booking> stream, String state) {
        var time = LocalDateTime.now();
        switch (state) {
            case "CURRENT":
                stream = stream.filter(it -> it.getStart().isBefore(time) && it.getEnd().isAfter(time));
                break;
            case "PAST":
                stream = stream.filter(it -> it.getEnd().isBefore(time));
                break;
            case "FUTURE":
                stream = stream.filter(it -> it.getStart().isAfter(time));
                break;
            case "WAITING":
                stream = stream.filter(it -> it.getStatus().equals(Status.WAITING));
                break;
            case "REJECTED":
                stream = stream.filter(it -> it.getStatus().equals(Status.REJECTED));
                break;
            case "ALL":
                break;
            default:
                throw new LockedException("Unknown state: UNSUPPORTED_STATUS");
        }
        return stream.sorted(Comparator.comparingLong(it -> -it.getStart().toInstant(ZoneOffset.UTC).toEpochMilli()))
                .map(bookingMapper::toDTO)
                .collect(Collectors.toList());
    }

    private void checkValidBookingTime(BookingIncome bookingIncome) {
        if (bookingIncome.getStart() == null || bookingIncome.getEnd() == null) {
            throw new LockedException("Wrong time");
        }
        if (bookingIncome.getEnd().isBefore(bookingIncome.getStart()) ||
                bookingIncome.getStart().isBefore(LocalDateTime.now()) ||
                bookingIncome.getEnd().isBefore(LocalDateTime.now()) ||
                bookingIncome.getStart().isEqual(bookingIncome.getEnd())) {
            throw new LockedException("Wrong time");
        }
    }
}
