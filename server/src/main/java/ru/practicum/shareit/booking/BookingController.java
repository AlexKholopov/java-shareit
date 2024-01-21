package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingIncome;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.utils.ConstantUtils;
import ru.practicum.shareit.utils.Marker;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @Validated(Marker.OnCreate.class)
    public BookingDto addBooking(@RequestBody BookingIncome bookingIncome, @RequestHeader(ConstantUtils.USER_ID) long user) {
        return bookingService.addBooking(bookingIncome, user);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@PathVariable long bookingId, @RequestParam boolean approved,
                                     @RequestHeader(ConstantUtils.USER_ID) long user) {
        return bookingService.approveBooking(bookingId, approved, user);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader(ConstantUtils.USER_ID) long user, @PathVariable long bookingId) {
        return bookingService.getBooking(user, bookingId);
    }

    @GetMapping
    public List<BookingDto> getAllBookings(@RequestParam(required = false, defaultValue = ConstantUtils.DEFAULT_FROM) int from,
                                           @RequestParam(required = false, defaultValue = ConstantUtils.DEFAULT_SIZE) int size,
                                           @RequestHeader(ConstantUtils.USER_ID) long user,
                                           @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllUserBookings(from, size, user, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsByOwner(@RequestParam(required = false, defaultValue = ConstantUtils.DEFAULT_FROM) int from,
                                                  @RequestParam(required = false, defaultValue = ConstantUtils.DEFAULT_SIZE) int size,
                                                  @RequestHeader(ConstantUtils.USER_ID) long user,
                                                  @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllUsersItemsBookings(from, size, user, state);
    }
}
