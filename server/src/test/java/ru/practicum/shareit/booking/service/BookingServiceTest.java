package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import ru.practicum.shareit.user.model.dto.UserId;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
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
    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    public void prepare() {
        owner = new User();
        owner.setId(1L);

        booker = new User();
        booker.setId(2L);

        item = new Item();
        item.setId(1L);
        item.setOwner(owner);
    }

    @Test
    void addBookingSuccess() {
        var start = LocalDateTime.now().plusMinutes(1);
        var end = LocalDateTime.now().plusMinutes(2);

        item.setAvailable(true);

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
        booker.setId(1L);

        item.setAvailable(true);

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

        item.setAvailable(false);

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

        item.setAvailable(true);

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
        owner.setId(2L);

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
    void approveBookingThrowLockedException() {
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
    void getAllUsersItemsBookingsSuccessCurrent() {
        Booking booking = new Booking();
        booking.setStatus(Status.APPROVED);
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(booker);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        Mockito.when(bookingRepository.findByItem_OwnerAndStartBeforeAndEndAfter(Mockito.any(User.class), Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), Mockito.any(PageRequest.class)))
                .thenReturn(List.of(booking));

        var result = bookingService.getAllUsersItemsBookings(0, 2, 1L, "CURRENT");

        assertEquals(List.of(bookingMapper.toDTO(booking)), result);
    }

    @Test
    void getAllUsersItemsBookingsSuccessPast() {
        Booking booking = new Booking();
        booking.setStatus(Status.APPROVED);
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(booker);
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        Mockito.when(bookingRepository.findByItem_OwnerAndEndBefore(Mockito.any(User.class), Mockito.any(LocalDateTime.class), Mockito.any(PageRequest.class)))
                .thenReturn(List.of(booking));

        var result = bookingService.getAllUsersItemsBookings(0, 2, 2L, "PAST");

        assertEquals(List.of(bookingMapper.toDTO(booking)), result);
    }

    @Test
    void getAllUsersItemsBookingsSuccessFuture() {
        Booking booking = new Booking();
        booking.setStatus(Status.APPROVED);
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(booker);
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        Mockito.when(bookingRepository.findByItem_OwnerAndStartAfter(Mockito.any(User.class), Mockito.any(LocalDateTime.class), Mockito.any(PageRequest.class)))
                .thenReturn(List.of(booking));

        var result = bookingService.getAllUsersItemsBookings(0, 2, 2L, "FUTURE");

        assertEquals(List.of(bookingMapper.toDTO(booking)), result);
    }

    @Test
    void getAllUsersItemsBookingsSuccessWaiting() {
        Booking booking = new Booking();
        booking.setStatus(Status.APPROVED);
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(booker);
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        Mockito.when(bookingRepository.findByItem_OwnerAndStatus(booker, Status.WAITING, PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "start"))))
                .thenReturn(List.of(booking));

        var result = bookingService.getAllUsersItemsBookings(0, 2, 2L, "WAITING");

        assertEquals(List.of(bookingMapper.toDTO(booking)), result);
    }

    @Test
    void getAllUsersItemsBookingsSuccessRejected() {
        Booking booking = new Booking();
        booking.setStatus(Status.APPROVED);
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(booker);
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        Mockito.when(bookingRepository.findByItem_OwnerAndStatus(booker, Status.REJECTED, PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "start"))))
                .thenReturn(List.of(booking));

        var result = bookingService.getAllUsersItemsBookings(0, 2, 2L, "REJECTED");

        assertEquals(List.of(bookingMapper.toDTO(booking)), result);
    }

    @Test
    void getAllUsersItemsBookingsSuccessAll() {
        Booking booking = new Booking();
        booking.setStatus(Status.APPROVED);
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(booker);
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        Mockito.when(bookingRepository.findByItem_Owner(booker, PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "start"))))
                .thenReturn(List.of(booking));

        var result = bookingService.getAllUsersItemsBookings(0, 2, 2L, "ALL");

        assertEquals(List.of(bookingMapper.toDTO(booking)), result);
    }

    @Test
    void getAllUsersItemsBookingsFailValidation() {
        var result = assertThrows(ValidationException.class,
                () -> bookingService.getAllUsersItemsBookings(-1, 2, 2L, "ALL"));
        assertEquals("Incorrect page query", result.getMessage());
    }

    /*
    @Test
    void getAllUsersItemsBookingsFailUnsupportedStatus() {
        User booker = new User();
        booker.setId(2L);

        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(booker));

        var result = assertThrows(LockedException.class,
                () -> bookingService.getAllUsersItemsBookings(0, 2, 2L, "INCORRECT"));
        assertEquals("Unknown state: UNSUPPORTED_STATUS", result.getMessage());
    }

     */

    @Test
    void getAllUserBookingsSuccessCurrent() {
        Booking booking = new Booking();
        booking.setStatus(Status.APPROVED);
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(booker);
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        Mockito.when(bookingRepository.findByBookerAndStartBeforeAndEndAfter(Mockito.any(User.class), Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), Mockito.any(PageRequest.class)))
                .thenReturn(List.of(booking));

        var result = bookingService.getAllUserBookings(0, 2, 2L, "CURRENT");

        assertEquals(List.of(bookingMapper.toDTO(booking)), result);
    }

    @Test
    void getAllUserBookingsSuccessPast() {
        Booking booking = new Booking();
        booking.setStatus(Status.APPROVED);
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(booker);
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        Mockito.when(bookingRepository.findByBookerAndEndBefore(Mockito.any(User.class), Mockito.any(LocalDateTime.class), Mockito.any(PageRequest.class)))
                .thenReturn(List.of(booking));

        var result = bookingService.getAllUserBookings(0, 2, 2L, "PAST");

        assertEquals(List.of(bookingMapper.toDTO(booking)), result);
    }

    @Test
    void getAllUserBookingsSuccessFuture() {
        Booking booking = new Booking();
        booking.setStatus(Status.APPROVED);
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(booker);
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        Mockito.when(bookingRepository.findByBookerAndStartAfter(Mockito.any(User.class), Mockito.any(LocalDateTime.class), Mockito.any(PageRequest.class)))
                .thenReturn(List.of(booking));

        var result = bookingService.getAllUserBookings(0, 2, 2L, "FUTURE");

        assertEquals(List.of(bookingMapper.toDTO(booking)), result);
    }

    @Test
    void getAllUserBookingsSuccessWaiting() {
        Booking booking = new Booking();
        booking.setStatus(Status.APPROVED);
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(booker);
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        Mockito.when(bookingRepository.findByBookerAndStatus(booker, Status.WAITING, PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "start"))))
                .thenReturn(List.of(booking));

        var result = bookingService.getAllUserBookings(0, 2, 2L, "WAITING");

        assertEquals(List.of(bookingMapper.toDTO(booking)), result);
    }

    @Test
    void getAllUserBookingsSuccessRejected() {
        Booking booking = new Booking();
        booking.setStatus(Status.APPROVED);
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(booker);
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        Mockito.when(bookingRepository.findByBookerAndStatus(booker, Status.REJECTED, PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "start"))))
                .thenReturn(List.of(booking));

        var result = bookingService.getAllUserBookings(0, 2, 2L, "REJECTED");

        assertEquals(List.of(bookingMapper.toDTO(booking)), result);
    }

    @Test
    void getAllUserBookingsSuccessAll() {
        Booking booking = new Booking();
        booking.setStatus(Status.APPROVED);
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(booker);
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        Mockito.when(bookingRepository.findByBooker(booker, PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "start"))))
                .thenReturn(List.of(booking));

        var result = bookingService.getAllUserBookings(0, 2, 2L, "ALL");

        assertEquals(List.of(bookingMapper.toDTO(booking)), result);
    }

    @Test
    void getAllUserBookingsFailValidation() {
        var result = assertThrows(ValidationException.class,
                () -> bookingService.getAllUserBookings(-1, 2, 2L, "ALL"));
        assertEquals("Incorrect page query", result.getMessage());
    }
/*
    @Test
    void getAllUserBookingsFailUnsupportedStatus() {
        User booker = new User();
        booker.setId(2L);

        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(booker));

        var result = assertThrows(LockedException.class,
                () -> bookingService.getAllUserBookings(0, 2, 2L, "INCORRECT"));
        assertEquals("Unknown state: UNSUPPORTED_STATUS", result.getMessage());
    }

 */
}