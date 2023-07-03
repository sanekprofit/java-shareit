package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBooker_IdOrderByStartDesc(Long userId);

    List<Booking> findAllByBooker_IdAndStatusOrderByStartDesc(Long userId, BookingStatus status);

    List<Booking> findAllByBooker_IdAndStartAfterAndEndBeforeOrderByStartDesc(Long userId,
                                                                              LocalDateTime start,
                                                                              LocalDateTime end);

    List<Booking> findAllByBooker_IdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime start);

    List<Booking> findAllByBooker_IdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime end);

    List<Booking> findAllByItem_Owner_IdOrderByStartDesc(Long userId);

    List<Booking> findAllByItem_Owner_IdAndStatusOrderByStartDesc(Long userId, BookingStatus status);

    List<Booking> findAllByItem_Owner_IdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime start);

    List<Booking> findAllByItem_Owner_IdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime end);

    List<Booking> findAllByItem_Owner_IdAndStartAfterAndEndBeforeOrderByStartDesc(Long userId,
                                                                                  LocalDateTime start,
                                                                                  LocalDateTime end);

    List<Booking> findFirst2ByItem_IdAndStartBeforeAndStatusOrderByStartDesc(Long itemId,
                                                                            LocalDateTime end,
                                                                            BookingStatus status);

    List<Booking> findFirst2ByItem_IdAndStartGreaterThanEqualAndStatusOrderByStartAsc(Long itemId,
                                                                  LocalDateTime start,
                                                                  BookingStatus status);

    List<Booking> findAllByItem_Owner_IdAndStartBeforeAndStatusOrderByStartDesc(Long userId,
                                                                                        LocalDateTime end,
                                                                                        BookingStatus status);

    List<Booking> findAllByItem_Owner_IdAndStartAfterAndStatusOrderByStartAsc(Long userId,
                                                                                         LocalDateTime start,
                                                                                         BookingStatus status);

    Booking findFirstByBooker_IdAndEndAfterOrderByStartDesc(Long itemId, LocalDateTime end);
}