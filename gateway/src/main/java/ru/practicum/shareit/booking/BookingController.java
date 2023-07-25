package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.UnsupportedStatusException;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader(value = "X-Sharer-User-Id", required = false, defaultValue = "0")
                                                  long userId,
                                              @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                              @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @RequestParam(name = "size", defaultValue = "20") Integer size) {
        if (from < 0) {
            throw new IllegalArgumentException("Параметр from не может быть " + from);
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Параметр size не может быть " + size);
        }
        BookingState.from(stateParam)
                .orElseThrow(() -> new UnsupportedStatusException("Unknown state: " + stateParam));
        log.info(String.format("Get booking with state %s, userId=%d, from=%d, size=%d", stateParam, userId, from, size));
        return bookingClient.getBookings(userId, stateParam, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsOwner(@RequestHeader(value = "X-Sharer-User-Id", required = false, defaultValue = "0")
                                                       long userId,
                                                   @RequestParam(name = "state", required = false, defaultValue = "ALL") String stateParam,
                                                   @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
                                                   @RequestParam(name = "size", required = false, defaultValue = "20") Integer size) {
        if (from < 0) {
            throw new IllegalArgumentException("Параметр from не может быть " + from);
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Параметр size не может быть " + size);
        }
        BookingState.from(stateParam)
                .orElseThrow(() -> new UnsupportedStatusException("Unknown state: " + stateParam));
        log.info(String.format("Получен запрос на получение списка аренд владельца с id %d State: %s", userId, stateParam));
        return bookingClient.getBookingsOwner(userId, stateParam, from, size);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBookingStatus(@RequestHeader(value = "X-Sharer-User-Id", required = false, defaultValue = "0")
                                       long userId,
                                       @PathVariable("bookingId") Long bookingId,
                                       @RequestParam Boolean approved) {
        log.info(String.format("Получен запрос на обновление статуса аренды. approved = %b", approved));
        return bookingClient.updateBookingStatus(userId, bookingId, approved);
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(value = "X-Sharer-User-Id", required = false, defaultValue = "0")
                                                    long userId,
                                           @RequestBody BookItemRequestDto requestDto) {
        log.info(String.format("Creating booking %s, userId=%d", requestDto, userId));
        return bookingClient.createBooking(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(value = "X-Sharer-User-Id", required = false, defaultValue = "0")
                                                 long userId,
                                             @PathVariable Long bookingId) {
        log.info(String.format("Get booking %d, userId=%d", bookingId, userId));
        return bookingClient.getBooking(userId, bookingId);
    }
}