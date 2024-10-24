package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Override
    @EntityGraph("booking.item")
    Optional<Booking> findById(Long aLong);

    @Override
    @EntityGraph("booking.item")
    Booking save(Booking booking);

    Page<Booking> getAllByBookerId(Long userId, Pageable pageable);

    @Query(value = "select b from Booking  b where b.booker.id = :userId and :currentDate between b.start and b.end ")
    @EntityGraph("booking.booker")
    Page<Booking> getAllCurrentBookingByBookerId(@Param("userId") Long userId,
                                                 @Param("currentDate") LocalDateTime currentDate, Pageable pageable);

    Page<Booking> getAllByBookerIdAndEndBefore(Long userId, LocalDateTime currentDate, Pageable pageable);

    Page<Booking> getAllByBookerIdAndEndAfter(Long userId, LocalDateTime currentDate, Pageable pageable);

    Page<Booking> getAllByBookerIdAndStatus(Long userId, BookingStatus status, Pageable pageable);

    @Query(value = "select b from Booking b left join Item i on b.item.id = i.id where b.item.owner.id = :userId")
    @EntityGraph("booking.item")
    Page<Booking> getAllByOwnerId(@Param("userId") Long userId, Pageable pageable);

    @Query(value = "select b from Booking  b left join Item  i on b.item.id = i.id where b.item.owner.id = :userId " +
            "and b.start < :currentDate")
    @EntityGraph("booking.item")
    Page<Booking> getAllByOwnerIdAndEndBefore(@Param("userId") Long userId,
                                              @Param("currentDate") LocalDateTime currentDate, Pageable pageable);

    @Query(value = "select b from Booking b left join Item i on b.item.id = i.id where b.item.owner.id = :userId " +
            "and (:currentDate between b.start and b.end)")
    @EntityGraph("booking.item")
    Page<Booking> getAllCurrentBookingByOwnerId(@Param("userId") Long userId,
                                                @Param("currentDate") LocalDateTime currentDate, Pageable pageable);

    @Query(value = "select b from Booking  b left join Item  i on b.item.id = i.id where b.item.owner.id = :userId " +
            "and b.start > :currentDate")
    @EntityGraph("booking.item")
    Page<Booking> getAllByOwnerIdAndEndAfter(@Param("userId") Long userId,
                                              @Param("currentDate") LocalDateTime currentDate, Pageable pageable);

    @Query(value = "select b from  Booking b left join Item i on b.item.id = i.id where b.item.owner.id = :userId " +
            "and b.status = :status")
    @EntityGraph("booking.item")
    Page<Booking> getAllByOwnerIdAndStatus(@Param("userId") Long userId, @Param("status") BookingStatus status,
                                           Pageable pageable);

    boolean existsByItemIdAndStatusAndEndAfter(Long itemId, BookingStatus status, LocalDateTime currentDate);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND (b.end <= :now OR b.end = :now) ORDER BY b.start DESC limit 1")
    Booking findByItemIdAndEndBeforeOrEqualsOrderByStartDesc(@Param("itemId") Long itemId, @Param("now") LocalDateTime now);


    Booking getByItemIdAndEndBeforeOrderByStartDesc(Long itemId, LocalDateTime currentDate);

    Booking getByItemIdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime currentDate);

}
