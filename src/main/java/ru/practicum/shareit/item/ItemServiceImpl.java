package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ItemServiceImpl implements ItemService {

    private final UserService userService;
    private long generatorId = 0;
    Map<Long, Item> items = new HashMap<>();

    public ItemServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Item createItem(Long userId, Item item) {
        validationCheck(userId, item);
        if (userService.getUser(userId) == null) {
            throw new NotFoundException("Пользователя с id " + userId + " не существует.");
        }
        generatorId++;
        item.setId(generatorId);
        item.setOwner(userId);
        items.put(generatorId, item);
        return item;
    }

    @Override
    public Item updateItem(Long userId, Item itemCurrent, Long itemId) {
        validationCheckPatch(userId);
        if (userService.getUser(userId) == null) {
            throw new NotFoundException("Пользователя с id " + userId + " не существует.");
        }
        if (items.get(itemId).getOwner() != userId) {
            throw new NotFoundException("Пользователь с id " + userId + " не создавал такой товар.");
        }
        Item itemUpdated = items.get(itemId);
        if (itemCurrent.getName() != null) {
            itemUpdated.setName(itemCurrent.getName());
        }
        if (itemCurrent.getDescription() != null) {
            itemUpdated.setDescription(itemCurrent.getDescription());
        }
        if (itemCurrent.getAvailable() != null) {
            itemUpdated.setAvailable(itemCurrent.getAvailable());
        }
        items.put(itemId, itemUpdated);
        return itemUpdated;
    }

    @Override
    public Item getItem(Long userId, Long itemId) {
        if (userService.getUser(userId) == null) {
            throw new NotFoundException("Пользователя с id " + userId + " не существует.");
        }
        return items.get(itemId);
    }

    @Override
    public List<Item> getItems(Long userId) {
        List<Item> userItems = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner() == userId) {
                userItems.add(item);
            }
        }
        return userItems;
    }

    @Override
    public List<Item> getItemSearch(Long userId, String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        String searchText = text.toLowerCase();
        List<Item> itemResult = new ArrayList<>();
        for (Item item : items.values()) {
            String itemName = item.getName().toLowerCase();
            String itemDescription = item.getDescription().toLowerCase();
            if ((itemName.contains(searchText) || itemDescription.contains(searchText)) &&
                    item.getAvailable()) {
                itemResult.add(item);
            }
        }
        return itemResult;
    }


    private void validationCheck(Long userId, Item item) {
        if (item.toString().contains("description=null") || item.toString().contains("name=null") ||
        item.getName().isBlank()) {
            throw new ValidationException("Описание товара или название не может быть пустым.");
        }
        if (item.toString().contains("available=null")) {
            throw new ValidationException("Поле available обязательно.");
        }
        if (userId == 0) {
            throw new ValidationException("Не был указан id пользователя.");
        }
    }

    private void validationCheckPatch(Long userId) {
        if (userId == 0) {
            throw new ValidationException("Не был указан id пользователя.");
        }
    }
}