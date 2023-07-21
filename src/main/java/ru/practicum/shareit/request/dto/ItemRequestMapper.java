package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemShort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.List;


public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest request, List<Item> items) {
        List<ItemShort> itemShorts = new ArrayList<>();
        for (Item item : items) {
            ItemShort itemShort = new ItemShort();
            itemShort.setId(item.getId());
            itemShort.setName(item.getName());
            itemShort.setDescription(item.getDescription());
            itemShort.setAvailable(item.getAvailable());
            itemShort.setRequestId(item.getRequest().getId());
            itemShorts.add(itemShort);
        }
        return new ItemRequestDto(request.getId(), request.getDescription(), request.getCreated(), itemShorts);
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest request) {
        return new ItemRequestDto(request.getId(), request.getDescription(), request.getCreated());
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest request, Item item) {
        ItemShort itemShort = new ItemShort();
        itemShort.setId(item.getId());
        itemShort.setName(item.getName());
        itemShort.setDescription(item.getDescription());
        itemShort.setAvailable(item.getAvailable());
        itemShort.setRequestId(item.getRequest().getId());
        return new ItemRequestDto(request.getId(), request.getDescription(), request.getCreated(), itemShort);
    }
}
