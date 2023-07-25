package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBooker_IdOrderByStartDesc(Long userId, Pageable pageable);

    List<Booking> findAllByBooker_IdAndStatusOrderByStartDesc(Long userId, BookingStatus status, Pageable pageable);

    List<Booking> findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId,
                                                                              LocalDateTime start,
                                                                              LocalDateTime end,
                                                                              Pageable pageable);

    List<Booking> findAllByBooker_IdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByBooker_IdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByItem_Owner_IdOrderByStartDesc(Long userId, Pageable pageable);

    List<Booking> findAllByItem_Owner_IdAndStatusOrderByStartDesc(Long userId, BookingStatus status, Pageable pageable);

    List<Booking> findAllByItem_Owner_IdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime start, Pageable pageable);

    List<Booking> findAllByItem_Owner_IdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId,
                                                                                  LocalDateTime start,
                                                                                  LocalDateTime end,
                                                                                  Pageable pageable);

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

    Booking findFirstByBooker_IdAndEndAfterOrderByStartDesc(Long userId, LocalDateTime end);
}