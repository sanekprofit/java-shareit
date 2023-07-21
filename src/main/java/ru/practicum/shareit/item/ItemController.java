package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentShort;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShort;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemShort createItem(@RequestHeader(value = "X-Sharer-User-Id", required = false, defaultValue = "0") Long userId,
                           @RequestBody ItemShort itemShort) {
        log.info("Получен запрос на создание товара: {}", itemShort);
        return itemService.createItem(userId, itemShort);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(value = "X-Sharer-User-Id", required = false, defaultValue = "0")
                                        Long userId,
                                 @PathVariable("itemId") Long itemId,
                                 @RequestBody CommentShort commentShort) {
        log.info("Получен запрос на добавление комментария от юзера " + userId + " на предмет с id: " + itemId);
        return itemService.createComment(userId, itemId, commentShort);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@RequestHeader(value = "X-Sharer-User-Id", required = false, defaultValue = "0") Long userId,
                           @RequestBody Item item,
                           @PathVariable("itemId") Long itemId) {
        log.info("Получен запрос на обновление товара: {}", item);
        return itemService.updateItem(userId, item, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable("itemId") Long itemId) {
        log.info("Получен запрос на получение товара: {}", itemId);
        return itemService.getItem(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
                                  @RequestParam(name = "size", required = false, defaultValue = "20") Integer size) {
        log.info("Получен запрос на получение списка товаров");
        return itemService.getItems(userId, from, size);
    }

    @GetMapping("/search")
    public List<Item> getItemSearch(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @RequestParam("text") String text,
                                    @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
                                    @RequestParam(name = "size", required = false, defaultValue = "20") Integer size) {
        log.info("Получен запрос на получение товара по поиску: {}", text);
        return itemService.getItemSearch(userId, text, from, size);
    }
}
