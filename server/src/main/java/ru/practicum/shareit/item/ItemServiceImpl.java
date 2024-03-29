package ru.practicum.shareit.item;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.*;

@Repository
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository requestRepository;

    public ItemServiceImpl(ItemRepository repository,
                           UserService userService,
                           BookingRepository bookingRepository,
                           CommentRepository commentRepository, ItemRequestRepository requestRepository) {
        this.repository = repository;
        this.userService = userService;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.requestRepository = requestRepository;
    }

    @Override
    public ItemShort createItem(Long userId, ItemShort itemShort) {
        validationCheck(userId, itemShort);
        userService.getUser(userId);
        Item item = ItemMapper.toItem(itemShort);
        item.setOwner(userService.getUser(userId));
        ItemRequest itemRequest = requestRepository.findFirstById(itemShort.getRequestId());
        item.setRequest(itemRequest);
        repository.save(item);
        itemShort.setId(item.getId());
        return itemShort;
    }

    @Override
    public CommentDto createComment(Long userId, Long itemId, CommentShort commentShort) {
        if (commentShort.getText().isBlank()) {
            throw new ValidationException("Текст комментария не может быть пустым.");
        }
        Booking booking = bookingRepository.findFirstByBooker_IdAndEndAfterOrderByStartDesc(
                userId, LocalDateTime.now());
        if (booking == null) {
            throw new NotFoundException(String.format("Предмет с id %d никогда не брали в аренду.", itemId));
        }
        if (booking.getBooker().getId() != userId) {
            throw new ValidationException(String.format("Неправильный id юзера: %d", userId));
        }
        if (!booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new ValidationException("Статус аренды должен быть подтверждённым.");
        }
        if (booking.getStart().isAfter(LocalDateTime.now().plusDays(3))) {
            throw new ValidationException("Аренда ещё не началась.");
        }
        commentRepository.save(CommentMapper.toComment(booking, commentShort.getText()));
        return CommentMapper.toCommentDto(booking, commentShort.getText());
    }

    @Override
    public Item updateItem(Long userId, Item itemCurrent, Long itemId) {
        validationCheckPatch(userId);
        userService.getUser(userId);
        if (repository.findById(itemId).isEmpty()) {
            throw new NotFoundException(String.format("Предмет с id %d не существует.", itemId));
        }
        if (getItem(userId, itemId).getOwner().getId() != userId) {
            throw new NotFoundException(String.format("Пользователь с id %d не создавал такой товар.", userId));
        }
        Item itemUpdated = repository.findById(itemId).get();
        if (itemCurrent.getName() != null) {
            itemUpdated.setName(itemCurrent.getName());
        }
        if (itemCurrent.getDescription() != null) {
            itemUpdated.setDescription(itemCurrent.getDescription());
        }
        if (itemCurrent.getAvailable() != null) {
            itemUpdated.setAvailable(itemCurrent.getAvailable());
        }
        return repository.save(itemUpdated);
    }

    @Override
    public ItemDto getItem(Long userId, Long itemId) {
        userService.getUser(userId);
        if (repository.findById(itemId).isEmpty()) {
            throw new NotFoundException(String.format("Предмет с id %d не существует.", itemId));
        }
        List<Comment> comments = commentRepository.findAllByItem_Id(itemId);
        List<CommentDto> commentDto = new ArrayList<>();
        for (Comment comment : comments) {
            commentDto.add(CommentMapper.toCommentDto(comment));
        }
        List<Booking> bookingsLastList = bookingRepository.findFirst2ByItem_IdAndStartBeforeAndStatusOrderByStartDesc(
                itemId, LocalDateTime.now(), BookingStatus.APPROVED);
        List<Booking> bookingsNextList = bookingRepository.findFirst2ByItem_IdAndStartGreaterThanEqualAndStatusOrderByStartAsc(
                itemId, LocalDateTime.now(), BookingStatus.APPROVED);
        if (!bookingsLastList.isEmpty() && !bookingsNextList.isEmpty()) {
            Booking bookingLast = bookingsLastList.get(0);
            Booking bookingNext = bookingsNextList.get(0);
            if (bookingNext.getItem().getOwner().getId() == userId) {
                return ItemMapper.toItemDto(bookingNext, bookingLast, commentDto);
            }
        } else if (bookingsNextList.isEmpty() && !bookingsLastList.isEmpty()) {
            Booking bookingLast = bookingsLastList.get(0);
            if (bookingLast.getItem().getOwner().getId() == userId) {
                return ItemMapper.toItemDto(bookingLast, commentDto);
            }
        } else if (bookingsLastList.isEmpty() && !bookingsNextList.isEmpty()) {
            Booking bookingNext = bookingsNextList.get(0);
            if (bookingNext.getItem().getOwner().getId() == userId) {
                return ItemMapper.toItemDto(bookingNext.getItem(), bookingNext, commentDto);
            }
        }
        return ItemMapper.toItemDto(repository.findById(itemId).get(), commentDto);
    }

    @Override
    public List<ItemDto> getItems(Long userId, Integer from, Integer size) {
        int fromPage = from / size;
        PageRequest pageRequest = PageRequest.of(fromPage, size);
        List<ItemDto> itemDtoList = new ArrayList<>();
        List<List<CommentDto>> commentDto = new ArrayList<>();
        List<List<Comment>> comments = commentRepository.findAllByItem_Owner_Id(userId);
        for (List<Comment> comments1 : comments) {
            for (Comment comment : comments1) {
                commentDto.add(List.of(CommentMapper.toCommentDto(comment)));
            }
        }
        List<Booking> bookingsLastList = bookingRepository.findAllByItem_Owner_IdAndStartBeforeAndStatusOrderByStartDesc(
                userId, LocalDateTime.now(), BookingStatus.APPROVED);
        List<Booking> bookingsNextList = bookingRepository.findAllByItem_Owner_IdAndStartAfterAndStatusOrderByStartAsc(
                userId, LocalDateTime.now(), BookingStatus.APPROVED);
        if (!bookingsLastList.isEmpty() && !bookingsNextList.isEmpty()) {
            if (bookingsNextList.size() > bookingsLastList.size()) {
                while (bookingsNextList.size() != bookingsLastList.size()) {
                    bookingsNextList.remove(bookingsNextList.size() - 1);
                }
            } else if (bookingsLastList.size() > bookingsNextList.size()) {
                while (bookingsLastList.size() != bookingsNextList.size()) {
                    bookingsLastList.remove(bookingsLastList.size() - 1);
                }
            }
            if (comments.isEmpty()) {
                for (Booking bookingNext : bookingsNextList) {
                    for (Booking bookingLast : bookingsLastList) {
                        itemDtoList.add(ItemMapper.toItemDto(bookingNext, bookingLast, null));
                        for (Item item : repository.findAllByOwner_Id(userId, pageRequest)) {
                            if (item.getId() != (bookingLast.getItem().getId() & bookingNext.getItem().getId()))
                                itemDtoList.add(ItemMapper.toItemDto(item, null));
                        }
                    }
                }
            } else {
                for (Booking bookingNext : bookingsNextList) {
                    for (Booking bookingLast : bookingsLastList) {
                        for (List<CommentDto> comments1 : commentDto) {
                            itemDtoList.add(ItemMapper.toItemDto(bookingNext, bookingLast, comments1));
                            for (Item item : repository.findAllByOwner_Id(userId, pageRequest)) {
                                if (item.getId() != (bookingLast.getItem().getId() & bookingNext.getItem().getId()))
                                    itemDtoList.add(ItemMapper.toItemDto(item, comments1));
                            }
                        }
                    }
                }
            }
            return itemDtoList;
        } else {
            if (comments.isEmpty()) {
                for (Item item : repository.findAllByOwner_Id(userId, pageRequest)) {
                    itemDtoList.add(ItemMapper.toItemDto(item, null));
                }
            } else {
                for (List<CommentDto> comments1 : commentDto) {
                    for (Item item : repository.findAllByOwner_Id(userId, pageRequest)) {
                        itemDtoList.add(ItemMapper.toItemDto(item, comments1));
                    }
                }
            }
        }
        return itemDtoList;
    }

    @Override
    public List<Item> getItemSearch(Long userId, String text, Integer from, Integer size) {
        if (from < 0) {
            throw new ValidationException(String.format("Параметр from не может быть %d", from));
        }
        if (size <= 0) {
            throw new ValidationException(String.format("Параметр size не может быть %d", from));
        }
        int fromPage = from / size;
        PageRequest pageRequest = PageRequest.of(fromPage, size);
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return repository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(text, text, pageRequest);
    }

    private void validationCheck(Long userId, ItemShort itemShort) {
        if (itemShort.toString().contains("description=null") || itemShort.toString().contains("name=null") ||
        itemShort.getName().isBlank()) {
            throw new ValidationException("Описание товара или название не может быть пустым.");
        }
        if (itemShort.toString().contains("available=null")) {
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