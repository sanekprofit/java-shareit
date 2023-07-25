package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    Booking createBooking(BookingDto booking, Long userId);

    Booking updateBookingStatus(Long userId, Long bookingId, Boolean approved);

    Booking getBooking(Long userId, Long bookingId);

    List<Booking> getBookings(Long userId, String state, Integer from, Integer size);

    List<Booking> getBookingsOwner(Long userId, String state, Integer from, Integer size);
}