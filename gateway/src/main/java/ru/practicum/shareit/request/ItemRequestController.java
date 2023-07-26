package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader(value = "X-Sharer-User-Id", required = false, defaultValue = "0")
                                         long userId,
                                            @RequestBody ItemRequestDto itemRequest) {
        log.info("Получен запрос на добавление запроса для вещи: {}", itemRequest);
        return requestClient.createItemRequest(userId, itemRequest);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequestsOwn(@RequestHeader(value = "X-Sharer-User-Id", required = false, defaultValue = "0")
                                                   long userId) {
        log.info("Получен запрос на получение своих запросов и ответов на них. userId: {}", userId);
        return requestClient.getItemRequestsOwn(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getItemRequests(@RequestHeader(value = "X-Sharer-User-Id", required = false, defaultValue = "0")
                                                long userId,
                                                @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
                                                @RequestParam(name = "size", required = false, defaultValue = "20") Integer size) {
        if (from < 0) {
            throw new IllegalArgumentException("Параметр from не может быть " + from);
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Параметр size не может быть " + size);
        }
        log.info("Получен запрос на получение всех запросов. from & size = [ " + from + " | " + size + " ]");
        return requestClient.getItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(@RequestHeader(value = "X-Sharer-User-Id", required = false, defaultValue = "0")
                                         long userId,
                                         @PathVariable("requestId") Long requestId) {
        log.info("Получен запрос на получение запроса с id {}", requestId);
        return requestClient.getItemRequest(userId, requestId);
    }
}
