package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Query("from Booking b where b.item.owner.id = :ownerId and b.end < :now")
    List<Booking> bookingsForItemPast(int ownerId, LocalDateTime now, Pageable pageable);

    @Query("from Booking b where b.item.owner.id = :ownerId and b.start > :now")
    List<Booking> bookingsForItemFuture(int ownerId, LocalDateTime now, Pageable pageable);

    @Query("from Booking b where b.item.owner.id = :ownerId and b.end >= :now and b.start < :now")
    List<Booking> bookingsForItemCurrent(int ownerId, LocalDateTime now, Pageable pageable);

    @Query("from Booking b where b.item.owner.id = :ownerId and b.status = :status")
    List<Booking> bookingsByItemOwnerAndStatus(int ownerId, BookingStatus status, Pageable pageable);

    @Query("from Booking b where b.item.owner.id = :ownerId")
    List<Booking> bookingsForItem(int ownerId, Pageable pageable);

    List<Booking> findAllByBookerId(int bookerId, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatus(int bookerId, BookingStatus status, Pageable pageable);

    List<Booking> findByBookerIdAndEndBefore(int userId, LocalDateTime now, Pageable pageable);

    List<Booking> findByBookerIdAndStartAfter(int userId, LocalDateTime now, Pageable pageable);

    List<Booking> findByBookerIdAndEndIsAfterAndStartIsBefore(int userId, LocalDateTime end, LocalDateTime start, Pageable pageable);

    Optional<Booking> findFirstByItem_IdAndStartBefore(int itemId, LocalDateTime nowTime, Sort sort);

    Optional<Booking> findFirstByItem_IdAndStartAfterAndStatusNot(int itemId, LocalDateTime nowTime, BookingStatus status, Sort sort);

    Collection<Booking> findAllByItemInAndStartBefore(Collection<Item> items, LocalDateTime nowTime, Sort sort);

    Collection<Booking> findAllByItemInAndStartAfterAndStatusNot(Collection<Item> items, LocalDateTime nowTime, BookingStatus bookingStatus, Sort sort);

    Optional<Booking> findFirstByItem_IdAndBooker_IdAndEndBefore(int itemId, int userId, LocalDateTime now);

    @Query("select count(1)>0 from Booking b where status = :bookingStatus" +
            " and ((b.start between :startTime and :endTime) or (b.end between :startTime and :endTime))")
    boolean existsByStatusAndStartBetweenOrEndBetween(BookingStatus bookingStatus, LocalDateTime startTime, LocalDateTime endTime);
}
