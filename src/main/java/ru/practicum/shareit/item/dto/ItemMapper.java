package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                Item.builder()
                        .name(item.getName())
                        .description(item.getDescription())
                        .available(item.getAvailable())
                        .request(item.getRequest())
                        .build());
    }
}