package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item createItem(Long userId, Item item);

    Item updateItem(Long userId, Item item, Long itemId);

    Item getItem(Long userId, Long itemId);

    List<Item> getItems(Long userId);

    List<Item> getItemSearch(Long userId, String text);
}