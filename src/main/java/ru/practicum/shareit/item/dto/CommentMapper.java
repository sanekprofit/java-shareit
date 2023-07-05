package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Comment;

public class CommentMapper {
    public static Comment toComment(Booking booking, String text) {
        return new Comment(text, booking.getItem(), booking.getBooker());
    }

    public static CommentDto toCommentDto(Booking booking, String text) {
        return new CommentDto(text, booking.getBooker().getName());
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getText(), comment.getAuthor().getName());
    }
}
