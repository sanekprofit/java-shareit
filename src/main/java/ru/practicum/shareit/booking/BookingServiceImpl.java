package ru.practicum.shareit.booking;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository repository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    public BookingServiceImpl(BookingRepository repository, UserService userService, ItemRepository itemRepository) {
        this.repository = repository;
        this.userService = userService;
        this.itemRepository = itemRepository;
    }

    @Override
    public Booking createBooking(BookingDto bookingDto, Long userId) {
        userService.getUser(userId);
        if (bookingDto.getItemId() == userId) {
            throw new NotFoundException(String.format("Нельзя взять в аренду свой же предмет. userId: %d", userId));
        }
        Booking booking = toBooking(bookingDto, userId);
        validationCheck(booking);
        return repository.save(booking);
    }

    @Override
    public Booking updateBookingStatus(Long userId, Long bookingId, Boolean approved) {
        if (repository.findById(bookingId).isEmpty()) {
            throw new NotFoundException(String.format("Бронирования с id %d не существует.", bookingId));
        }
        Booking booking = repository.findById(bookingId).get();
        if (booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException(String.format("Пользователь с id %d не является владельцем этой вещи.", userId));
        }
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new ValidationException("Статус уже подтверждён.");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return repository.save(booking);
    }

    @Override
    public Booking getBooking(Long userId, Long bookingId) {
        userService.getUser(userId);
        bookingUserCheck(userId, bookingId);
        return repository.findById(bookingId).get();
    }

    @Override
    public List<Booking> getBookings(Long userId, String state, Integer from, Integer size) {
        if (from < 0) {
            throw new ValidationException(String.format("Параметр from не может быть %d", from));
        }
        if (size <= 0) {
            throw new ValidationException(String.format("Параметр size не может быть %d", from));
        }
        userService.getUser(userId);
        List<Booking> bookings;
        int fromPage = from / size;
        PageRequest pageRequest = PageRequest.of(fromPage, size);
        switch (state) {
            case "ALL":
                bookings = repository.findAllByBooker_IdOrderByStartDesc(userId, pageRequest);
                break;
            case "CURRENT":
                bookings = repository.findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                        LocalDateTime.now(), LocalDateTime.now(), pageRequest);
                break;
            case "FUTURE":
                bookings = repository.findAllByBooker_IdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), pageRequest);
                break;
            case "PAST":
                bookings = repository.findAllByBooker_IdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), pageRequest);
                break;
            case "WAITING":
                bookings = repository.findAllByBooker_IdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING, pageRequest);
                break;
            case "REJECTED":
                bookings = repository.findAllByBooker_IdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED, pageRequest);
                break;
            default:
                throw new ValidationException(String.format("Unknown state: %s", state));
        }
        return bookings;
    }

    @Override
    public List<Booking> getBookingsOwner(Long userId, String state, Integer from, Integer size) {
        if (from < 0) {
            throw new ValidationException(String.format("Параметр from не может быть %d", from));
        }
        if (size <= 0) {
            throw new ValidationException(String.format("Параметр size не может быть %d", from));
        }
        userService.getUser(userId);
        List<Booking> bookings;
        int fromPage = from / size;
        PageRequest pageRequest = PageRequest.of(fromPage, size);
        switch (state) {
            case "ALL":
                bookings = repository.findAllByItem_Owner_IdOrderByStartDesc(userId, pageRequest);
                break;
            case "CURRENT":
                bookings = repository.findAllByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(
                        userId, LocalDateTime.now(), LocalDateTime.now(), pageRequest);
                break;
            case "FUTURE":
                bookings = repository.findAllByItem_Owner_IdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now(), pageRequest);
                break;
            case "PAST":
                bookings = repository.findAllByItem_Owner_IdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now(), pageRequest);
                break;
            case "WAITING":
                bookings = repository.findAllByItem_Owner_IdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING, pageRequest);
                break;
            case "REJECTED":
                bookings = repository.findAllByItem_Owner_IdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED, pageRequest);
                break;
            default:
                throw new ValidationException(String.format("Unknown state: %s", state));
        }
        return bookings;
    }

    private void validationCheck(Booking booking) {
        if (!booking.getItem().getAvailable()) {
            throw new ValidationException("Предмет должен быть доступен для бронирования.");
        }
        if (booking.toString().contains("start=null") ||
                booking.toString().contains("end=null") ||
                booking.getStart().isEqual(booking.getEnd()) ||
                booking.getEnd().isBefore(booking.getStart()) ||
                booking.getStart().isBefore(LocalDateTime.now()) ||
                booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Предмет имеет неправильную дату конца или начала аренды.");
        }
    }

    private void bookingUserCheck(Long userId, Long bookingId) {
        if (repository.findById(bookingId).isEmpty()) {
            throw new NotFoundException(String.format("Бронирования с id %d не существует.", bookingId));
        }
        Booking booking = repository.findById(bookingId).get();
        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException(String.format("Неправильный id пользователя: %d", userId));
        }
    }

    private Booking toBooking(BookingDto bookingDto, Long userId) {
        if (itemRepository.findById(bookingDto.getItemId()).isEmpty()) {
            throw new NotFoundException(String.format("Предмет с id %d не существует.", bookingDto.getItemId()));
        }
        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(itemRepository.findById(bookingDto.getItemId()).get());
        booking.setBooker(userService.getUser(userId));
        return booking;
    }
}