package ru.practicum.shareit.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestShort;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository repository;
    private final ItemRepository itemRepository;
    private final UserService userService;

    public ItemRequestServiceImpl(ItemRequestRepository repository, ItemRepository itemRepository, UserService userService) {
        this.repository = repository;
        this.itemRepository = itemRepository;
        this.userService = userService;
    }

    @Override
    public ItemRequest createItemRequest(Long userId, ItemRequestShort itemRequestShort) {
        User requester = userService.getUser(userId);
        if (itemRequestShort.toString().contains("description=null") || itemRequestShort.getDescription().isBlank()) {
            throw new ValidationException("Описание не может быть пустым.");
        }
        ItemRequest newRequest = new ItemRequest();
        newRequest.setDescription(itemRequestShort.getDescription());
        newRequest.setRequester(requester);
        return repository.save(newRequest);
    }

    @Override
    public List<ItemRequestDto> getItemRequestsOwn(Long userId) {
        userService.getUser(userId);
        List<ItemRequestDto> itemRequestDtos = new ArrayList<>();
        List<ItemRequest> itemRequests = repository.findAllByRequester_Id(userId);
        if (itemRequests.isEmpty()) {
            return new ArrayList<>();
        }
        for (ItemRequest itemRequest : itemRequests) {
            List<Item> items = itemRepository.findAllByRequest_Id(itemRequest.getId());
            if (!items.isEmpty()) {
                itemRequestDtos.add(ItemRequestMapper.toItemRequestDto(itemRequest, items));
            } else {
                itemRequestDtos.add(ItemRequestMapper.toItemRequestDto(itemRequest));
            }
        }
        return itemRequestDtos;
    }

    @Override
    public List<ItemRequestDto> getItemRequests(Long userId, Integer from, Integer size) {
        userService.getUser(userId);
        List<ItemRequestDto> itemRequestDtos = new ArrayList<>();
        int fromPage = from / size;
        PageRequest pageRequest = PageRequest.of(fromPage, size);
        List<ItemRequest> requestsIter = repository.findAllByRequester_IdNotOrderByCreatedDesc(userId, pageRequest);
        for (ItemRequest itemRequest : requestsIter) {
            List<Item> items = itemRepository.findAllByRequest_Id(itemRequest.getId());
            if (!items.isEmpty()) {
                itemRequestDtos.add(ItemRequestMapper.toItemRequestDto(itemRequest, items));
            } else {
                itemRequestDtos.add(ItemRequestMapper.toItemRequestDto(itemRequest));
            }
        }
        return itemRequestDtos;
    }

    @Override
    public ItemRequestDto getItemRequest(Long userId, Long requestId) {
        userService.getUser(userId);
        ItemRequest itemRequest = repository.findFirstById(requestId);
        ItemRequestDto itemRequestDto;
        if (itemRequest != null) {
            Item item = itemRepository.findFirstByRequest_Id(requestId);
            if (item != null) {
                itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest, item);
            } else {
                itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
            }
        } else {
            throw new NotFoundException("Запрос на предмет с id " + requestId + " не найден.");
        }
        return itemRequestDto;
    }
}
