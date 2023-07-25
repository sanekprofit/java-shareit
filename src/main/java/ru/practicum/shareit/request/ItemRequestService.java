package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShort;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequest createItemRequest(Long userId, ItemRequestShort itemRequestShort);

    List<ItemRequestDto> getItemRequestsOwn(Long userId);

    List<ItemRequestDto> getItemRequests(Long userId, Integer from, Integer size);

    ItemRequestDto getItemRequest(Long userId, Long requestId);
}
