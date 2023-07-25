package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShort;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService service;

    public ItemRequestController(ItemRequestService service) {
        this.service = service;
    }

    @PostMapping
    public ItemRequest createItemRequest(@RequestHeader(value = "X-Sharer-User-Id", required = false, defaultValue = "0")
                                             Long userId,
                                         @RequestBody ItemRequestShort itemRequestShort) {
        log.info("Получен запрос на добавление запроса для вещи: {}", itemRequestShort);
        return service.createItemRequest(userId, itemRequestShort);
    }

    @GetMapping
    public List<ItemRequestDto> getItemRequestsOwn(@RequestHeader(value = "X-Sharer-User-Id", required = false, defaultValue = "0")
                                                    Long userId) {
        log.info("Получен запрос на получение своих запросов и ответов на них. userId: {}", userId);
        return service.getItemRequestsOwn(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getItemRequests(@RequestHeader(value = "X-Sharer-User-Id", required = false, defaultValue = "0")
                                                 Long userId,
                                             @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
                                             @RequestParam(name = "size", required = false, defaultValue = "20") Integer size) {
        log.info("Получен запрос на получение всех запросов. from & size = [ " + from + " | " + size + " ]");
        return service.getItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequest(@RequestHeader(value = "X-Sharer-User-Id", required = false, defaultValue = "0")
                                          Long userId,
                                      @PathVariable("requestId") Long requestId) {
        log.info("Получен запрос на получение запроса с id {}", requestId);
        return service.getItemRequest(userId, requestId);
    }
}