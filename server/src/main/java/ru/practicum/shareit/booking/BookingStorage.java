package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.dto.ItemBookingInfo;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingStorage extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerId(long bookerId);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(long bookerId,
                                                                          LocalDateTime currentTime1,
                                                                          LocalDateTime currentTime2);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(long bookerId, LocalDateTime startDate);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(long bookerId, LocalDateTime endDate);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(long bookerId, BookingStatus status);


    List<Booking> findByBookerIdAndItemIdAndStatusAndEndBeforeOrderByStartDesc(long bookerId, long itemId,
                                                                               BookingStatus status,
                                                                               LocalDateTime endDate);

    @Query("SELECT b FROM Booking b WHERE b.item.ownerId = :ownerId")
    List<Booking> findAllByOwnerId(@Param("ownerId") long ownerId);

    @Query("SELECT b FROM Booking b WHERE " +
            "b.item.ownerId = :ownerId AND " +
            "b.start < :currentTime AND " +
            "b.end > :currentTime")
    List<Booking> findCurrentByOwnerId(@Param("ownerId") long ownerId,
                                       @Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT b FROM Booking b WHERE " +
            "b.item.ownerId = :ownerId AND " +
            "b.start > :afterTime")
    List<Booking> findFutureByOwnerId(@Param("ownerId") long ownerId,
                                      @Param("afterTime") LocalDateTime afterTime);

    @Query("SELECT b FROM Booking b WHERE " +
            "b.item.ownerId = :ownerId AND " +
            "b.end < :beforeTime")
    List<Booking> findPastByOwnerId(@Param("ownerId") long ownerId,
                                    @Param("beforeTime") LocalDateTime beforeTime);

    @Query("SELECT b FROM Booking b WHERE " +
            "b.item.ownerId = :ownerId AND " +
            "b.status = :status " +
            "ORDER BY b.start DESC")
    List<Booking> findByOwnerIdAndStatusOrderByStartDesc(@Param("ownerId") long ownerId,
                                                         @Param("status") BookingStatus status);

    @Query("select new ru.practicum.shareit.booking.dto.ItemBookingInfo(" +
            "b.item.id, " +
            "MAX(CASE WHEN b.end < :currentTime THEN b.end ELSE NULL END), " +
            "MIN(CASE WHEN b.start > :currentTime THEN b.start ELSE NULL END)) " +
            "From Booking b " +
            "WHERE b.item.id IN :ids AND b.status = 'APPROVED' " +
            "GROUP BY b.item.id"
    )
    List<ItemBookingInfo> findItemBookingInfo(@Param("ids") Collection<Long> ids,
                                              @Param("currentTime") LocalDateTime currentTime);
}
