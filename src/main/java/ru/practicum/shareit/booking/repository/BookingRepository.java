package ru.practicum.shareit.booking.repository;


import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker(User booker, Sort sort);

    List<Booking> findByBookerAndStartBeforeAndEndAfter(User booker, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findByBookerAndEndBefore(User booker, LocalDateTime end, Sort sort);

    List<Booking> findByBookerAndStartAfter(User booker, LocalDateTime start, Sort sort);

    List<Booking> findByBookerAndStatus(User booker, Status status, Sort sort);


    List<Booking> findByItem_Owner(User owner, Sort sort);

    List<Booking> findByItem_OwnerAndStartBeforeAndEndAfter(User owner, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findByItem_OwnerAndEndBefore(User owner, LocalDateTime end, Sort sort);

    List<Booking> findByItem_OwnerAndStartAfter(User owner, LocalDateTime start, Sort sort);

    List<Booking> findByItem_OwnerAndStatus(User owner, Status status, Sort sort);

    @Query("select case when count(b)> 0 then true else false end " +
            "from Booking as b " +
            "where b.booker = ?1 and b.item = ?2 and status = 'APPROVED' and start < ?3")
    boolean existsAcceptedByBookerAndItemAndTime(User booker, Item item, LocalDateTime time);

    List<Booking> findByItemInAndStartLessThanEqualAndStatus(Collection<Item> items, LocalDateTime start, Status approved);

    List<Booking> findByItemInAndStartAfterAndStatus(Collection<Item> items, LocalDateTime start, Status approved);

    List<Booking> findByItemAndStartLessThanEqualAndStatus(Item item, LocalDateTime start, Status approved);

    List<Booking> findByItemAndStartAfterAndStatus(Item item, LocalDateTime start, Status approved);


}
