package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ItemShort {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private long requestId;

    public ItemShort(String name, String description, Boolean available, long requestId) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.requestId = requestId;
    }
}
