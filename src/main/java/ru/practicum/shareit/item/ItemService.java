package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentShort;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShort;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemShort createItem(Long userId, ItemShort itemShort);

    CommentDto createComment(Long userId, Long itemId, CommentShort commentShort);

    Item updateItem(Long userId, Item item, Long itemId);

    ItemDto getItem(Long userId, Long itemId);

    List<ItemDto> getItems(Long userId, Integer from, Integer size);

    List<Item> getItemSearch(Long userId, String text, Integer from, Integer size);
}