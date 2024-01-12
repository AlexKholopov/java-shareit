package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.model.dto.BookingIncome;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.LockedException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserId;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @InjectMocks
    private BookingService bookingService;
    @Spy
    private ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);
    @Spy
    private BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);


    @Test
    void addBookingSuccess() {
        var start = LocalDateTime.now().plusMinutes(1);
        var end = LocalDateTime.now().plusMinutes(2);

        User owner = new User();
        owner.setId(1L);

        User booker = new User();
        booker.setId(2L);

        Item item = new Item();
        item.setId(0L);
        item.setAvailable(true);
        item.setOwner(owner);

        BookingIncome bookingIncome = new BookingIncome();
        bookingIncome.setItemId(1L);
        bookingIncome.setStart(start);
        bookingIncome.setEnd(end);

        UserId userId = new UserId();
        userId.setId(2L);
        Booking bookingDto = new Booking();
        bookingDto.setId(0L);
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        bookingDto.setItem(item);
        bookingDto.setStatus(Status.WAITING);
        bookingDto.setBooker(booker);


        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        bookingService.addBooking(bookingIncome, 2L);


        verify(bookingRepository).save(bookingDto);
    }

    @Test
    void addBookingThrowNotFoundException() {
        var start = LocalDateTime.now().plusMinutes(1);
        var end = LocalDateTime.now().plusMinutes(2);

        User owner = new User();
        owner.setId(1L);

        User booker = new User();
        booker.setId(1L);

        Item item = new Item();
        item.setId(0L);
        item.setAvailable(true);
        item.setOwner(owner);

        BookingIncome bookingIncome = new BookingIncome();
        bookingIncome.setItemId(1L);
        bookingIncome.setStart(start);
        bookingIncome.setEnd(end);


        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(booker));
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        var exception = assertThrows(NotFoundException.class,
                () -> bookingService.addBooking(bookingIncome, 1L));

        assertEquals("You can't book your item", exception.getMessage());
    }

    @Test
    void addBookingThrowLockedException() {
        var start = LocalDateTime.now().plusMinutes(1);
        var end = LocalDateTime.now().plusMinutes(2);

        User owner = new User();
        owner.setId(1L);

        User booker = new User();
        booker.setId(2L);

        Item item = new Item();
        item.setId(0L);
        item.setAvailable(false);
        item.setOwner(owner);

        BookingIncome bookingIncome = new BookingIncome();
        bookingIncome.setItemId(1L);
        bookingIncome.setStart(start);
        bookingIncome.setEnd(end);


        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        var exception = assertThrows(LockedException.class,
                () -> bookingService.addBooking(bookingIncome, 2L));

        assertEquals("Item not available", exception.getMessage());
    }

    @Test
    void addBookingFailValidation() {
        var start = LocalDateTime.now().plusMinutes(1);
        var end = LocalDateTime.now().minusMinutes(2);

        User owner = new User();
        owner.setId(1L);

        User booker = new User();
        booker.setId(2L);

        Item item = new Item();
        item.setId(0L);
        item.setAvailable(true);
        item.setOwner(owner);

        BookingIncome bookingIncome = new BookingIncome();
        bookingIncome.setItemId(1L);
        bookingIncome.setStart(start);
        bookingIncome.setEnd(end);


        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        var exception = assertThrows(ValidationException.class,
                () -> bookingService.addBooking(bookingIncome, 2L));

        assertEquals("Wrong time", exception.getMessage());
    }

    @Test
    void approveBookingSuccessApproved() {
        User owner = new User();
        owner.setId(1L);
        Item item = new Item();
        item.setId(1L);
        item.setOwner(owner);
        Booking booking = new Booking();
        booking.setStatus(Status.WAITING);
        booking.setId(1L);
        booking.setItem(item);
        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        bookingService.approveBooking(1L, true, 1L);

        booking.setStatus(Status.APPROVED);
        verify(bookingRepository).save(booking);
    }

    @Test
    void approveBookingSuccessRejected() {
        User owner = new User();
        owner.setId(1L);
        Item item = new Item();
        item.setId(1L);
        item.setOwner(owner);
        Booking booking = new Booking();
        booking.setStatus(Status.WAITING);
        booking.setId(1L);
        booking.setItem(item);
        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        bookingService.approveBooking(1L, false, 1L);

        booking.setStatus(Status.REJECTED);
        verify(bookingRepository).save(booking);
    }

    @Test
    void approveBookingThrowNotFoundException() {
        User owner = new User();
        owner.setId(2L);
        Item item = new Item();
        item.setId(1L);
        item.setOwner(owner);
        Booking booking = new Booking();
        booking.setStatus(Status.WAITING);
        booking.setId(1L);
        booking.setItem(item);
        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        var exception = assertThrows(NotFoundException.class, () -> bookingService.approveBooking(1L, true, 1L));
        assertEquals("Request denied, you don't have access rights", exception.getMessage());
    }

    @Test
    void approveBookingThrow() {
        User owner = new User();
        owner.setId(1L);
        Item item = new Item();
        item.setId(1L);
        item.setOwner(owner);
        Booking booking = new Booking();
        booking.setStatus(Status.REJECTED);
        booking.setId(1L);
        booking.setItem(item);
        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        var exception = assertThrows(LockedException.class,
                () -> bookingService.approveBooking(1L, true, 1L));
        assertEquals("You cant change status twice", exception.getMessage());
    }

    @Test
    void getBookingSuccess() {
        User owner = new User();
        owner.setId(1L);
        User booker = new User();
        booker.setId(2L);
        Item item = new Item();
        item.setId(1L);
        item.setOwner(owner);
        Booking booking = new Booking();
        booking.setStatus(Status.REJECTED);
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(booker);
        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        var result = bookingService.getBooking(1L, 1L);

        assertEquals(bookingMapper.toDTO(booking), result);
    }

    @Test
    void getBookingThrowNotFoundException() {
        User owner = new User();
        owner.setId(1L);
        User booker = new User();
        booker.setId(2L);
        Item item = new Item();
        item.setId(1L);
        item.setOwner(owner);
        Booking booking = new Booking();
        booking.setStatus(Status.REJECTED);
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(booker);
        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item));


        var result = assertThrows(NotFoundException.class, () -> bookingService.getBooking(3L, 1L));
        assertEquals("Request denied, you don't have access rights", result.getMessage());
    }

    @Test
    void getAllUserBookings() {
    }

    @Test
    void getAllUsersItemsBookings() {
    }
}