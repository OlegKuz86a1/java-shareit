package ru.practicum.shareit.booking.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.common.BaseEntity;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@NamedEntityGraph(name = "booking.item", attributeNodes = {
        @NamedAttributeNode("item")
})
@NamedEntityGraph(name = "booking.booker", attributeNodes = {
        @NamedAttributeNode("booker")
})
@NamedEntityGraph(name = "booking.full", attributeNodes = {
        @NamedAttributeNode("item"), @NamedAttributeNode("booker")
})
public class Booking extends BaseEntity {

    @Column(name = "start_booking")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd, hh:mm:ss")
    private LocalDateTime start;

    @Column(name = "end_booking")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd, hh:mm:ss")
    private LocalDateTime end;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booker_id")
    private User booker;

    @CollectionTable(name = "booking_status", joinColumns = @JoinColumn(name = "booking_id"))
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
}

