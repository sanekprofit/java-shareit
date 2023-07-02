package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;

public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                Booking.builder()
                        .id(booking.getId())
                        .start(booking.getStart())
                        .end(booking.getEnd())
                        .status(booking.getStatus())
                        .item(booking.getItem())
                        .build()
        );
    }
}
