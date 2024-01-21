package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingIncome;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WebMvcTest(BookingController.class)
class BookingControllerTestMvc {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingService bookingService;

    private static final String USER_ID = "1";

    @SneakyThrows
    @Test
    void addBookingTestSuccess() {
        BookingIncome bookingIncome = new BookingIncome();
        bookingIncome.setStart(LocalDateTime.now().plusMinutes(1));
        bookingIncome.setEnd(LocalDateTime.now().plusMinutes(2));
        BookingDto dto = new BookingDto();
        Mockito.when(bookingService.addBooking(bookingIncome, 1L)).thenReturn(dto);

        var res = mockMvc.perform(MockMvcRequestBuilders.post("/bookings").header("X-Sharer-User-Id", USER_ID)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingIncome)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(dto), res);
    }


    /*
    @SneakyThrows
    @Test
    void addBookingTestFailValidation() {
        BookingIncome bookingIncome = new BookingIncome();
        bookingIncome.setStart(LocalDateTime.now().minusMinutes(1));
        bookingIncome.setEnd(LocalDateTime.now().plusMinutes(2));
        BookingDto dto = new BookingDto();
        Mockito.when(bookingService.addBooking(bookingIncome, 1L)).thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings").header("X-Sharer-User-Id", USER_ID)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingIncome)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

     */

    @SneakyThrows
    @Test
    void approveBooking() {
        mockMvc.perform(MockMvcRequestBuilders.patch("/bookings/{bookingId}?approved=true", 1L).header("X-Sharer-User-Id", USER_ID))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(bookingService).approveBooking(1L, true, 1L);
    }

    @SneakyThrows
    @Test
    void getBooking() {
        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/{bookingId}", 1L).header("X-Sharer-User-Id", USER_ID))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(bookingService).getBooking(1L, 1L);
    }

    @SneakyThrows
    @Test
    void getAllBookings() {
        mockMvc.perform(MockMvcRequestBuilders.get("/bookings?from=1&size=2&state=ALL", 1L).header("X-Sharer-User-Id", USER_ID))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(bookingService).getAllUserBookings(1, 2, 1L, "ALL");
    }

    @SneakyThrows
    @Test
    void getAllBookingsByOwner() {
        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner?from=1&size=2&state=ALL", 1L).header("X-Sharer-User-Id", USER_ID))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(bookingService).getAllUsersItemsBookings(1, 2, 1L, "ALL");
    }
}