package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingIncome;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.LockedException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserId;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    public BookingDto addBooking(BookingIncome bookingIncome, long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("No such user was found"));
        checkValidBookingTime(bookingIncome);
        Item item = itemRepository.findById(bookingIncome.getItemId()).orElseThrow(() -> new NotFoundException("No such item was found"));
        if (item.getOwner().getId() == userId) {
            throw new NotFoundException("You can't book your item");
        }
        if (!item.getAvailable()) {
            throw new LockedException("Item not available");
        }
        UserId booker = new UserId();
        booker.setId(userId);
        BookingDto bookingDto = bookingMapper.fromIncome(bookingIncome, itemMapper.toShort(item), booker, 0);
        bookingDto.setStatus(Status.WAITING);
        Booking booking = bookingMapper.fromDTO(bookingDto);
        Booking bookingFromDb = bookingRepository.save(booking);
        return bookingMapper.toDTO(bookingFromDb);
    }

    public BookingDto approveBooking(long bookingId, boolean approved, long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("No such booking was found"));
        Item item = itemRepository.findById(booking.getItem().getId()).orElseThrow(() -> new NotFoundException("No such item was found"));
        if (item.getOwner().getId() != userId) {
            throw new NotFoundException("Request denied, you don't have access rights");
        }
        if (booking.getStatus() != Status.WAITING) {
            throw new LockedException("You cant change status twice");
        }
        Status status;
        if (approved) {
            status = Status.APPROVED;
        } else {
            status = Status.REJECTED;
        }
        booking.setStatus(status);
        bookingRepository.save(booking);
        return bookingMapper.toDTO(booking);
    }

    public BookingDto getBooking(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("No such booking was found"));
        Item item = itemRepository.findById(booking.getItem().getId()).orElseThrow(() -> new NotFoundException("No such item was found"));
        if (booking.getBooker().getId() == userId || item.getOwner().getId() == userId) {
            return bookingMapper.toDTO(booking);
        } else {
            throw new NotFoundException("Request denied, you don't have access rights");
        }
    }

    public List<BookingDto> getAllUserBookings(long userId, String stateStr) {
        User booker = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("No such user was found"));
        State state;
        try {
            state = State.valueOf(stateStr);
        } catch (Exception e) {
            throw new LockedException("Unknown state: UNSUPPORTED_STATUS");
        }
        var bookings = new ArrayList<Booking>();
        switch (state) {
            case CURRENT:
                bookings.addAll(bookingRepository.findByBookerAndStartBeforeAndEndAfter(booker, LocalDateTime.now(), LocalDateTime.now()));
                break;
            case PAST:
                bookings.addAll(bookingRepository.findByBookerAndEndBefore(booker, LocalDateTime.now()));
                break;
            case FUTURE:
                bookings.addAll(bookingRepository.findByBookerAndStartAfter(booker, LocalDateTime.now()));
                break;
            case WAITING:
                bookings.addAll(bookingRepository.findByBookerAndStatus(booker, Status.WAITING));
                break;
            case REJECTED:
                bookings.addAll(bookingRepository.findByBookerAndStatus(booker, Status.REJECTED));
                break;
            case ALL:
                bookings.addAll(bookingRepository.findByBooker(booker));
                break;

        }
        return bookings.stream().sorted(Comparator.comparingLong(it -> -it.getStart().toInstant(ZoneOffset.UTC).toEpochMilli())).map(bookingMapper::toDTO).collect(Collectors.toList());
    }

    public List<BookingDto> getAllUsersItemsBookings(long ownerId, String stateStr) {
        User user = userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("No such user was found"));
        State state;
        try {
            state = State.valueOf(stateStr);
        } catch (Exception e) {
            throw new LockedException("Unknown state: UNSUPPORTED_STATUS");
        }
        var bookings = new ArrayList<Booking>();
        switch (state) {
            case CURRENT:
                bookings.addAll(bookingRepository.findByItem_OwnerAndStartBeforeAndEndAfter(user, LocalDateTime.now(), LocalDateTime.now()));
                break;
            case PAST:
                bookings.addAll(bookingRepository.findByItem_OwnerAndEndBefore(user, LocalDateTime.now()));
                break;
            case FUTURE:
                bookings.addAll(bookingRepository.findByItem_OwnerAndStartAfter(user, LocalDateTime.now()));
                break;
            case WAITING:
                bookings.addAll(bookingRepository.findByItem_OwnerAndStatus(user, Status.WAITING));
                break;
            case REJECTED:
                bookings.addAll(bookingRepository.findByItem_OwnerAndStatus(user, Status.REJECTED));
                break;
            case ALL:
                bookings.addAll(bookingRepository.findByItem_Owner(user));
                break;
        }
        return bookings.stream().sorted(Comparator.comparingLong(it -> -it.getStart().toInstant(ZoneOffset.UTC).toEpochMilli())).map(bookingMapper::toDTO).collect(Collectors.toList());
    }

    private void checkValidBookingTime(BookingIncome bookingIncome) {
        if (bookingIncome.getEnd().isBefore(bookingIncome.getStart()) || bookingIncome.getStart().isEqual(bookingIncome.getEnd())) {
            throw new LockedException("Wrong time");
        }
    }
}
