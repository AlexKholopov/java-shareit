package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingIncome;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;
    @InjectMocks
    private BookingController bookingController;

    @Test
    void addBooking() {
        BookingIncome bookingIncome = new BookingIncome();
        Mockito.when(bookingService.addBooking(bookingIncome, 1L)).thenReturn(new BookingDto());

        var booking = bookingController.addBooking(bookingIncome, 1L);

        assertEquals(new BookingDto(), booking);
    }

    @Test
    void approveBooking() {
        Mockito.when(bookingService.approveBooking(1L, true, 1L)).thenReturn(new BookingDto());

        var booking = bookingController.approveBooking(1L, true, 1L);

        assertEquals(new BookingDto(), booking);
    }

    @Test
    void getBooking() {
        Mockito.when(bookingService.getBooking(1L, 2L)).thenReturn(new BookingDto());

        var booking = bookingController.getBooking(1L, 2L);

        assertEquals(new BookingDto(), booking);
    }

    @Test
    void getAllBookings() {
        List<BookingDto> expectedList = List.of(new BookingDto());
        Mockito.when(bookingService.getAllUserBookings(1, 1, 3, "ALL")).thenReturn(expectedList);

        var result = bookingController.getAllBookings(1, 1, 3, "ALL");

        assertEquals(expectedList, result);
    }

    @Test
    void getAllBookingsByOwner() {
        List<BookingDto> expectedList = List.of(new BookingDto());
        Mockito.when(bookingService.getAllUsersItemsBookings(1, 1, 3, "ALL")).thenReturn(expectedList);

        var result = bookingController.getAllBookingsByOwner(1, 1, 3, "ALL");

        assertEquals(expectedList, result);
    }
}