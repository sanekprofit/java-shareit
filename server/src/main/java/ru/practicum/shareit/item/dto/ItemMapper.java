package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public class ItemMapper {

    public static ItemDto toItemDto(Booking bookingNext, Booking bookingLast, List<CommentDto> comments) {
        return new ItemDto(bookingNext, bookingLast, comments);
    }

    public static ItemDto toItemDto(Item item, List<CommentDto> comments) {
        return new ItemDto(item, comments);
    }

    public static ItemDto toItemDto(Item item, Booking bookingNext, List<CommentDto> comments) {
        return new ItemDto(item, bookingNext, comments);
    }

    public static ItemDto toItemDto(Booking bookingLast, List<CommentDto> comments) {
        return new ItemDto(bookingLast, comments);
    }

    public static Item toItem(ItemShort itemShort) {
        return new Item(itemShort.getName(), itemShort.getDescription(), itemShort.getAvailable());
    }
}