package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.LockedException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final ItemRepository itemRepository;

    public BookingDto addBooking(BookingDto bookingDto) {
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow();
        if (!item.getAvailable()) {
            throw new LockedException("Item not available");
        }
        bookingDto.setStatus(Status.WAITING);
        Booking booking = bookingMapper.fromDTO(bookingDto);
        bookingRepository.save(booking);
        return bookingDto;
    }
}
