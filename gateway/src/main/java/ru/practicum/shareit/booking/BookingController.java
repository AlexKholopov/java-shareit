package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.booking.dto.BookingIncome;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exceptions.LockedException;
import ru.practicum.shareit.utils.ConstantUtils;
import ru.practicum.shareit.utils.Marker;

import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestBody @Validated(Marker.OnCreate.class) BookingIncome bookingIncome,
                                             @RequestHeader(ConstantUtils.USER_ID) long user) {
        return bookingClient.bookItem(user, bookingIncome);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@PathVariable long bookingId, @RequestParam boolean approved,
                                                 @RequestHeader(ConstantUtils.USER_ID) long user) {
        return bookingClient.approveBooking(bookingId, user, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(ConstantUtils.USER_ID) long user, @PathVariable long bookingId) {
        return bookingClient.getBooking(user, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookings(@PositiveOrZero @RequestParam(required = false, defaultValue = ConstantUtils.DEFAULT_FROM) int from,
                                                 @PositiveOrZero @RequestParam(required = false, defaultValue = ConstantUtils.DEFAULT_SIZE) int size,
                                                 @RequestHeader(ConstantUtils.USER_ID) long user,
                                                 @RequestParam(defaultValue = "ALL") String state) {
        var stateValidated = validateState(state);
        return bookingClient.getBookings(user, stateValidated, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsByOwner(@PositiveOrZero @RequestParam(required = false, defaultValue = ConstantUtils.DEFAULT_FROM) int from,
                                                        @PositiveOrZero @RequestParam(required = false, defaultValue = ConstantUtils.DEFAULT_SIZE) int size,
                                                        @RequestHeader(ConstantUtils.USER_ID) long user,
                                                        @RequestParam(defaultValue = "ALL") String state) {
        var stateValidated = validateState(state);
        return bookingClient.getAllBookingsByOwner(from, size, user, stateValidated);
    }

    private BookingState validateState(String state) {
        try {
            return BookingState.valueOf(state);
        } catch (Throwable e) {
            throw new LockedException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

}
