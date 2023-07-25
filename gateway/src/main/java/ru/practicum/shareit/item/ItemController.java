package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(value = "X-Sharer-User-Id", required = false, defaultValue = "0") long userId,
                                     @RequestBody ItemDto item) {
        log.info(String.format("Получен запрос на создание товара: %s", item));
        return itemClient.createItem(userId, item);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(value = "X-Sharer-User-Id", required = false, defaultValue = "0")
                                    long userId,
                                                @PathVariable("itemId") Long itemId,
                                                @RequestBody CommentDto comment) {
        log.info(String.format("Получен запрос на добавление комментария от юзера %d на предмет с id: %d", userId, itemId));
        return itemClient.createComment(userId, itemId, comment);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(value = "X-Sharer-User-Id", required = false, defaultValue = "0") long userId,
                           @RequestBody ItemDto item,
                           @PathVariable("itemId") Long itemId) {
        log.info(String.format("Получен запрос на обновление товара: %s", item));
        return itemClient.updateItem(userId, item, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable("itemId") Long itemId) {
        log.info(String.format("Получен запрос на получение товара: %s", itemId));
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                  @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
                                  @RequestParam(name = "size", required = false, defaultValue = "20") Integer size) {
        if (from < 0) {
            throw new IllegalArgumentException("Параметр from не может быть " + from);
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Параметр size не может быть " + size);
        }
        log.info("Получен запрос на получение списка товаров");
        return itemClient.getItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemSearch(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @RequestParam("text") String text,
                                    @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
                                    @RequestParam(name = "size", required = false, defaultValue = "20") Integer size) {
        if (from < 0) {
            throw new IllegalArgumentException("Параметр from не может быть " + from);
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Параметр size не может быть " + size);
        }
        log.info(String.format("Получен запрос на получение товара по поиску: %s", text));
        return itemClient.getItemSearch(userId, text, from, size);
    }
}
