package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@Entity
public class Booking {
    @Id
    private final long id;
    @Column(name = "Requester")
    private long requesterId;
    @Column(name = "item")
    private long itemId;
    @Column(name = "start_time")
    private OffsetDateTime startTime;
    @Column(name = "end_time")
    private OffsetDateTime endTime;
    @Enumerated(EnumType.STRING)
    private Status status;
}
