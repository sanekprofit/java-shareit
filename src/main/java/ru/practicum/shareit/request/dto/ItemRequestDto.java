package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.dto.ItemShort;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestDto {
    private long id;
    private String description;
    private Instant created;
    private List<ItemShort> items = new ArrayList<>();

    public ItemRequestDto(long id, String description, Instant created) {
        this.id = id;
        this.description = description;
        this.created = created;
    }

    public ItemRequestDto(long id, String description, Instant created, ItemShort itemShort) {
        this.id = id;
        this.description = description;
        this.created = created;
        this.items.add(itemShort);
    }
}
