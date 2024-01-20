package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class TestBookingRepository {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;
    private PageRequest pageRequest = PageRequest.of(0, 3);
    private Booking booking;
    private User owner;
    private User user;
    private Item item;

    @BeforeEach
    void prepare() {
        owner = new User();
        owner.setName("Name");
        owner.setEmail("email@email.com");

        user = new User();
        user.setName("Name");
        user.setEmail("email2@email.com");

        item = new Item();
        item.setOwner(owner);
        item.setName("Отвертка");
        item.setDescription("Крутая отвертка");
        item.setAvailable(true);

        var startTime = LocalDateTime.now().plusMinutes(1);
        var endTime = LocalDateTime.now().plusMinutes(3);
        booking = new Booking();
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(Status.WAITING);
        booking.setStart(startTime);
        booking.setEnd(endTime);

        userRepository.save(owner);
        userRepository.save(user);
        itemRepository.save(item);
        bookingRepository.save(booking);
    }

    @Test
    public void testFindByBooker() {
        var expectedBookings = List.of(booking);
        var res = bookingRepository.findByBooker(user, pageRequest);

        assertEquals(expectedBookings, res);
    }

    @Test
    public void testExistingByBookerAndTime() {
        booking.setStatus(Status.APPROVED);
        bookingRepository.save(booking);
        var res = bookingRepository.existsAcceptedByBookerAndItemAndTime(user, item, LocalDateTime.now().plusMinutes(5));

        assertTrue(res);
    }

    @Test
    public void testFindByBookerAndStatus() {
        var expectedBookings = List.of(booking);
        booking.setStatus(Status.APPROVED);
        bookingRepository.save(booking);
        var res = bookingRepository.findByBookerAndStatus(user, Status.APPROVED, pageRequest);

        assertEquals(expectedBookings, res);
    }

    @Test
    public void testFindByItemOwner() {
        var expectedBookings = List.of(booking);
        booking.setStatus(Status.APPROVED);
        bookingRepository.save(booking);
        var res = bookingRepository.findByItem_Owner(owner, pageRequest);

        assertEquals(expectedBookings, res);
    }
}
