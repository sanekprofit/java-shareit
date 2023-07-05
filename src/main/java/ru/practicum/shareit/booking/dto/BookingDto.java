package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private String status;
    private long itemId;

    public BookingDto(Booking booking) {
        this.id = booking.getId();
        this.start = booking.getStart();
        this.end = booking.getEnd();
        this.status = String.valueOf(booking.getStatus());
        this.itemId = booking.getItem().getId();
    }
}