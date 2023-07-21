package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

@Data
public class ItemDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private BookingDto lastBooking = new BookingDto();
    private BookingDto nextBooking = new BookingDto();
    private List<CommentDto> comments;

    public ItemDto(Booking bookingNext, Booking bookingLast, List<CommentDto> comments) {
        this.id = bookingNext.getItem().getId();
        this.name = bookingNext.getItem().getName();
        this.description = bookingNext.getItem().getDescription();
        this.available = bookingNext.getItem().getAvailable();
        this.owner = bookingNext.getItem().getOwner();
        this.lastBooking.id = bookingLast.getId();
        this.lastBooking.bookerId = bookingLast.getBooker().getId();
        this.nextBooking.id = bookingNext.getId();
        this.nextBooking.bookerId = bookingNext.getBooker().getId();
        this.comments = comments;
    }

    public ItemDto(Item item, Booking bookingNext, List<CommentDto> comments) {
        this.id = item.getId();
        this.name = item.getName();
        this.description = item.getDescription();
        this.available = item.getAvailable();
        this.owner = item.getOwner();
        this.nextBooking.id = bookingNext.getId();
        this.nextBooking.bookerId = bookingNext.getBooker().getId();
        this.lastBooking = null;
        this.comments = comments;
    }

    public ItemDto(Booking bookingLast, List<CommentDto> comments) {
        this.id = bookingLast.getItem().getId();
        this.name = bookingLast.getItem().getName();
        this.description = bookingLast.getItem().getDescription();
        this.available = bookingLast.getItem().getAvailable();
        this.owner = bookingLast.getItem().getOwner();
        this.lastBooking.id = bookingLast.getId();
        this.lastBooking.bookerId = bookingLast.getBooker().getId();
        this.nextBooking = null;
        this.comments = comments;
    }

    public ItemDto(Item item, List<CommentDto> comments) {
        this.id = item.getId();
        this.name = item.getName();
        this.description = item.getDescription();
        this.available = item.getAvailable();
        this.owner = item.getOwner();
        this.lastBooking = null;
        this.nextBooking = null;
        this.comments = comments;
    }

    public ItemDto(String name, String description, Boolean available) {
        this.name = name;
        this.description = description;
        this.available = available;
    }

    @Data
    public static class BookingDto {
        Long id;
        Long bookerId;
    }
}