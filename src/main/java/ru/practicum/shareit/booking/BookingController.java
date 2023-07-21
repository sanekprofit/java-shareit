package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public Booking createBooking(@RequestBody BookingDto bookingDto,
                                 @RequestHeader(value = "X-Sharer-User-Id", required = false, defaultValue = "0")
                                    Long userId) {
        log.info("Получен запрос на добавление бронировавния: {}", bookingDto);
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public Booking updateBookingStatus(@RequestHeader(value = "X-Sharer-User-Id", required = false, defaultValue = "0")
                                     Long userId,
                                 @PathVariable("bookingId") Long bookingId,
                                 @RequestParam(name = "approved") Boolean approved) {
        log.info("Получен запрос на обновление статуса аренды.");
        return bookingService.updateBookingStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public Booking getBooking(@RequestHeader(value = "X-Sharer-User-Id", required = false, defaultValue = "0")
                                  Long userId,
                              @PathVariable("bookingId") Long bookingId) {
        log.info("Получен запрос на получение аренды.");
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<Booking> getBookings(@RequestHeader(value = "X-Sharer-User-Id", required = false, defaultValue = "0")
                                         Long userId,
                                     @RequestParam(name = "state", required = false, defaultValue = "ALL")
                                     String state,
                                     @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
                                     @RequestParam(name = "size", required = false, defaultValue = "20") Integer size) {
        log.info("Получен запрос на получение списка аренд пользователя с id " + userId + ", State: " + state);
        return bookingService.getBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<Booking> getBookingsOwner(@RequestHeader(
            value = "X-Sharer-User-Id", required = false, defaultValue = "0") Long userId,
                                          @RequestParam(name = "state", required = false, defaultValue = "ALL")
                                          String state,
                                          @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
                                          @RequestParam(name = "size", required = false, defaultValue = "20") Integer size) {
        log.info("Получен запрос на получение списка аренд владельца с id " + userId + ", State: " + state);
        return bookingService.getBookingsOwner(userId, state, from, size);
    }
}