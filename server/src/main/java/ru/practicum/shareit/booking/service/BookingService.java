package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingIncome;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.LockedException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserId;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
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

    public List<BookingDto> getAllUserBookings(int from, int size, long userId, String stateStr) {
        if (from < 0 || size < 0) {
            throw new ValidationException("Incorrect page query");
        }
        User booker = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("No such user was found"));
        State state = State.valueOf(stateStr);
        int page = from % size > 0 ? (from / size) + 1 : from / size;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "start"));
        List<Booking> bookings = List.of();
        switch (state) {
            case CURRENT:
                bookings = bookingRepository.findByBookerAndStartBeforeAndEndAfter(booker, LocalDateTime.now(), LocalDateTime.now(), pageRequest);
                break;
            case PAST:
                bookings = bookingRepository.findByBookerAndEndBefore(booker, LocalDateTime.now(), pageRequest);
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerAndStartAfter(booker, LocalDateTime.now(), pageRequest);
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerAndStatus(booker, Status.WAITING, pageRequest);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerAndStatus(booker, Status.REJECTED, pageRequest);
                break;
            case ALL:
                bookings = bookingRepository.findByBooker(booker, pageRequest);
                break;

        }
        return bookings.stream().map(bookingMapper::toDTO).collect(Collectors.toList());
    }

    public List<BookingDto> getAllUsersItemsBookings(int from, int size, long ownerId, String stateStr) {
        if (from < 0 || size < 0) {
            throw new ValidationException("Incorrect page query");
        }
        User user = userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("No such user was found"));
        State state = State.valueOf(stateStr);
        PageRequest pageRequest = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "start"));
        List<Booking> bookings = List.of();
        switch (state) {
            case CURRENT:
                bookings = bookingRepository.findByItem_OwnerAndStartBeforeAndEndAfter(user, LocalDateTime.now(), LocalDateTime.now(), pageRequest);
                break;
            case PAST:
                bookings = bookingRepository.findByItem_OwnerAndEndBefore(user, LocalDateTime.now(), pageRequest);
                break;
            case FUTURE:
                bookings = bookingRepository.findByItem_OwnerAndStartAfter(user, LocalDateTime.now(), pageRequest);
                break;
            case WAITING:
                bookings = bookingRepository.findByItem_OwnerAndStatus(user, Status.WAITING, pageRequest);
                break;
            case REJECTED:
                bookings = bookingRepository.findByItem_OwnerAndStatus(user, Status.REJECTED, pageRequest);
                break;
            case ALL:
                bookings = bookingRepository.findByItem_Owner(user, pageRequest);
                break;
        }
        return bookings.stream().map(bookingMapper::toDTO).collect(Collectors.toList());
    }

    private void checkValidBookingTime(BookingIncome bookingIncome) {
        if (bookingIncome.getEnd().isBefore(bookingIncome.getStart()) || bookingIncome.getStart().isEqual(bookingIncome.getEnd())) {
            throw new ValidationException("Wrong time");
        }
    }
}
