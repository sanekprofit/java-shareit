package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:shareit", webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTest {

    @MockBean
    private final BookingRepository repository;
    @MockBean
    private final UserRepository userRepository;
    @MockBean
    private final ItemRepository itemRepository;
    private final BookingService service;
    private User user;
    private User user1;
    private User user2;
    private Item item;
    private Item item1;
    private Booking bookingFuture;
    private Booking bookingPast;
    private Booking bookingVeryPast;

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "name", "email@email.com");
        user1 = new User(2L, "name1", "email1@email.com");
        user2 = new User(3L, "name2", "email2@email.com");
        item = new Item(1L, "name", "description", true);
        item1 = new Item(2L, "name1", "description1", true);
        bookingFuture = new Booking(LocalDateTime.now().plusDays(4L), LocalDateTime.now().plusDays(7L));
        bookingPast = new Booking(LocalDateTime.now().minusHours(3L), LocalDateTime.now().minusHours(1L));
        bookingVeryPast = new Booking(LocalDateTime.now().minusDays(3L), LocalDateTime.now().minusDays(1L));
    }

    @Test
    void createBookingTest() {
        item.setOwner(user);
        bookingFuture.setBooker(user1);
        bookingFuture.setItem(item);

        when(userRepository.findById(any()))
                .thenReturn(Optional.ofNullable(user1));

        when(itemRepository.findById(any()))
                .thenReturn(Optional.ofNullable(item));

        when(repository.save(any()))
                .thenReturn(bookingFuture);

        Booking booking = service.createBooking(BookingMapper.toBookingDto(bookingFuture), user1.getId());

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStart(), equalTo(bookingFuture.getStart()));
        assertThat(booking.getEnd(), equalTo(bookingFuture.getEnd()));
    }

    @Test
    void createBookingThrowNotFoundExceptionTest() {
        item.setOwner(user);
        bookingFuture.setBooker(user);
        bookingFuture.setItem(item);

        when(userRepository.findById(any()))
                .thenReturn(Optional.ofNullable(user));

        when(itemRepository.findById(any()))
                .thenReturn(Optional.ofNullable(item));

        assertThrows(NotFoundException.class, () -> service.createBooking(BookingMapper.toBookingDto(bookingFuture), 1L));
    }

    @Test
    void createBookingThrowItemNotAvailableExceptionTest() {
        item.setOwner(user);
        item.setAvailable(false);
        bookingFuture.setBooker(user1);
        bookingFuture.setItem(item);

        when(userRepository.findById(any()))
                .thenReturn(Optional.ofNullable(user1));

        when(itemRepository.findById(any()))
                .thenReturn(Optional.ofNullable(item));

        assertThrows(ValidationException.class, () -> service.createBooking(BookingMapper.toBookingDto(bookingFuture), user1.getId()));
    }

    @Test
    void createBookingThrowBadTimeExceptionTest() {
        item.setOwner(user);
        bookingVeryPast.setBooker(user1);
        bookingVeryPast.setItem(item);

        when(userRepository.findById(any()))
                .thenReturn(Optional.ofNullable(user1));

        when(itemRepository.findById(any()))
                .thenReturn(Optional.ofNullable(item));

        assertThrows(ValidationException.class, () -> service.createBooking(BookingMapper.toBookingDto(bookingVeryPast), user1.getId()));
    }

    @Test
    void createBookingThrowItemNotFoundExceptionTest() {
        item.setOwner(user);
        bookingVeryPast.setBooker(user1);
        bookingVeryPast.setItem(item);

        when(userRepository.findById(any()))
                .thenReturn(Optional.ofNullable(user1));

        assertThrows(NotFoundException.class, () -> service.createBooking(BookingMapper.toBookingDto(bookingVeryPast), user1.getId()));
    }

    @Test
    void updateBookingStatusTest() {
        item.setOwner(user);
        bookingFuture.setBooker(user1);
        bookingFuture.setItem(item);

        when(repository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(bookingFuture));

        when(repository.save(any()))
                .thenReturn(bookingFuture);

        Booking booking = service.updateBookingStatus(user.getId(), 1L, true);

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void updateBookingStatusThrowBookingNotFoundExceptionTest() {
        assertThrows(NotFoundException.class, () -> service.updateBookingStatus(1L, 99L, true));
    }

    @Test
    void updateBookingStatusThrowStatusIsConfirmedException() {
        item.setOwner(user);
        bookingFuture.setBooker(user1);
        bookingFuture.setItem(item);
        bookingFuture.setStatus(BookingStatus.APPROVED);

        when(repository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(bookingFuture));

        when(repository.save(any()))
                .thenReturn(bookingFuture);

        assertThrows(ValidationException.class, () -> service.updateBookingStatus(1L, 1L, true));
    }

    @Test
    void getBookingTest() {
        item.setOwner(user);
        bookingFuture.setBooker(user1);
        bookingFuture.setItem(item);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        when(repository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(bookingFuture));

        Booking booking = service.getBooking(1L, 1L);

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStart(), equalTo(bookingFuture.getStart()));
        assertThat(booking.getEnd(), equalTo(bookingFuture.getEnd()));
    }

    @Test
    void getBookingThrowUserNotFoundExceptionTest() {
        assertThrows(NotFoundException.class, () -> service.getBooking(99L, 1L));
    }

    @Test
    void getBookingThrowBookingNotFoundExceptionTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        assertThrows(NotFoundException.class, () -> service.getBooking(1L, 99L));
    }

    @Test
    void getBookingThrowNotFoundExceptionTest() {
        item.setOwner(user);
        bookingFuture.setBooker(user1);
        bookingFuture.setItem(item);
        when(userRepository.findById(3L))
                .thenReturn(Optional.ofNullable(user2));

        when(repository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(bookingFuture));

        assertThrows(NotFoundException.class, () -> service.getBooking(user2.getId(), 1L));
    }

    @Test
    void getBookingsThrowBadFromException() {
        assertThrows(ValidationException.class, () -> service.getBookings(1L, "ALL", -1, 20));
    }

    @Test
    void getBookingsThrowBadSizeException() {
        assertThrows(ValidationException.class, () -> service.getBookings(1L, "ALL", 0, -1));
    }

    @Test
    void getBookingsAllTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        when(repository.findAllByBooker_IdOrderByStartDesc(anyLong(), any()))
                .thenReturn(List.of(bookingPast));

        List<Booking> bookings = service.getBookings(user.getId(), "ALL", 0, 20);

        assertThat(bookings, hasSize(1));
    }

    @Test
    void getBookingsCurrentTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        when(repository.findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(), any(), any()))
                .thenReturn(List.of(bookingPast));

        List<Booking> bookings = service.getBookings(user.getId(), "CURRENT", 0, 20);

        assertThat(bookings, hasSize(1));
    }

    @Test
    void getBookingsFutureTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        when(repository.findAllByBooker_IdAndStartAfterOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(List.of(bookingFuture));

        List<Booking> bookings = service.getBookings(user.getId(), "FUTURE", 0, 20);

        assertThat(bookings, hasSize(1));
    }

    @Test
    void getBookingsPastTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        when(repository.findAllByBooker_IdAndEndBeforeOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(List.of(bookingPast));

        List<Booking> bookings = service.getBookings(user.getId(), "PAST", 0, 20);

        assertThat(bookings, hasSize(1));
    }
}
