package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.time.Instant;

@Data
public class CommentDto {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id = 1L;
    private String text;
    private String authorName;
    private Instant created;

    public CommentDto(String text, String authorName) {
        this.text = text;
        this.authorName = authorName;
        this.created = Instant.now();
    }
}