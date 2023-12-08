package ru.practicum.shareit.booking.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker(User booker);

    List<Booking> findByItem(Item item);

    @Query("select case when count(b)> 0 then true else false end " +
            "from Booking as b " +
            "where b.booker = ?1 and b.item = ?2 and status = 'APPROVED' and start < ?3")
    boolean existsAcceptedByBookerAndItemAndTime(User booker, Item item, LocalDateTime time);

    @Query("select b " +
            "from Booking as b " +
            "join b.item as i " +
            "where i.owner = ?1")
    List<Booking> findBookingsByItemOwner(long ownerId);

    List<Booking> findByItemInAndStartBeforeAndStatus(Collection<Item> items, LocalDateTime start, Status approved);

    List<Booking> findByItemInAndStartAfterAndStatus(Collection<Item> items, LocalDateTime start, Status approved);

    List<Booking> findByItemAndStartBeforeAndStatus(Item item, LocalDateTime start, Status approved);

    List<Booking> findByItemAndStartAfterAndStatus(Item item, LocalDateTime start, Status approved);


}
