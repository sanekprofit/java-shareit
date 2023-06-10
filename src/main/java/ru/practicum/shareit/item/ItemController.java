package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
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
    public Item createItem(@RequestHeader(value = "X-Sharer-User-Id", required = false, defaultValue = "0") Long userId,
                           @RequestBody Item item) {
        log.info("Получен запрос на создание товара.");
        return itemService.createItem(userId, item);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@RequestHeader(value = "X-Sharer-User-Id", required = false, defaultValue = "0") Long userId,
                           @RequestBody Item item,
                           @PathVariable("itemId") Long itemId) {
        log.info("Получен запрос на обновление товара.");
        return itemService.updateItem(userId, item, itemId);
    }

    @GetMapping("/{itemId}")
    public Item getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                        @PathVariable("itemId") Long itemId) {
        log.info("Получен запрос на получение товара.");
        return itemService.getItem(userId, itemId);
    }

    @GetMapping
    public List<Item> getItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на получение списка товаров");
        return itemService.getItems(userId);
    }

    @GetMapping("/search")
    public List<Item> getItemSearch(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @RequestParam("text") String text) {
        log.info("Получен запрос на получение товара по поиску.");
        return itemService.getItemSearch(userId, text);
    }
}
