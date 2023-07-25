package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
public class ItemServiceImplTest {

    private final ItemService service;
    @MockBean
    private final ItemRepository repository;
    @MockBean
    private final BookingRepository bookingRepository;
    @MockBean
    private final CommentRepository commentRepository;
    @MockBean
    private final UserRepository userRepository;
    private User user;
    private User user1;
    private User user2;
    private Item item;
    private Item item1;
    private ItemShort itemShort;
    private Booking bookingFuture;
    private Booking bookingPast;
    private Booking bookingVeryPast;
    private Comment comment;
    private CommentShort commentShort;

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "name", "email@email.com");
        user1 = new User(2L, "name1", "email1@email.com");
        user2 = new User(3L, "name2", "email2@email.com");
        item = new Item(1L, "name", "description", true);
        item1 = new Item(2L, "name1", "description1", true);
        itemShort = new ItemShort("name", "description", true, 0L);
        bookingFuture = new Booking(LocalDateTime.now().plusDays(4L), LocalDateTime.now().plusDays(7L));
        bookingPast = new Booking(LocalDateTime.now().minusHours(3L), LocalDateTime.now().minusHours(1L));
        bookingVeryPast = new Booking(LocalDateTime.now().minusDays(3L), LocalDateTime.now().minusDays(1L));
        comment = new Comment("text", item, user1);
        commentShort = new CommentShort("text");
    }

    @Test
    void createItemTest() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));

        service.createItem(user.getId(), itemShort);

        when(repository.findById(1L))
                .thenReturn(Optional.ofNullable(item));

        ItemDto item1 = service.getItem(user.getId(), 1L);

        assertThat(itemShort.getId(), notNullValue());
        assertThat(itemShort.getName(), equalTo(item1.getName()));
        assertThat(itemShort.getDescription(), equalTo(item1.getDescription()));
    }

    @Test
    void createItemThrowNotFoundExceptionTest() {
        when(userRepository.findById(any()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.createItem(99L, itemShort));
    }

    @Test
    void createItemThrowBadItemNameExceptionTest() {
        ItemShort badItem = new ItemShort(null, null, true, 0L);

        assertThrows(ValidationException.class, () -> service.createItem(1L, badItem));
    }

    @Test
    void createItemThrowAvailableExceptionTest() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));

        ItemShort badItem = new ItemShort("nameOk", "descriptionOk", null, 0L);

        assertThrows(ValidationException.class, () -> service.createItem(1L, badItem));
    }

    @Test
    void createItemThrowBadUserIdExceptionTest() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));

        assertThrows(ValidationException.class, () -> service.createItem(0L, itemShort));
    }

    @Test
    void createItemCommentTest() {
        item.setOwner(user);
        bookingPast.setBooker(user1);
        bookingPast.setItem(item);
        bookingPast.setStatus(BookingStatus.APPROVED);

        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));

        when(userRepository.findById(2L))
                .thenReturn(Optional.ofNullable(user1));

        when(repository.findById(1L))
                .thenReturn(Optional.ofNullable(item));

        when(bookingRepository.findFirstByBooker_IdAndEndAfterOrderByStartDesc(anyLong(), any()))
                .thenReturn(bookingPast);

        when(commentRepository.save(any()))
                .thenReturn(comment);

        CommentDto commentDto = CommentMapper.toCommentDto(comment);

        CommentDto commentDto1 = service.createComment(user1.getId(), 1L, commentShort);

        assertThat(commentDto1.getId(), notNullValue());
        assertThat(commentDto1.getText(), equalTo(commentDto.getText()));
        assertThat(commentDto1.getAuthorName(), equalTo(commentDto.getAuthorName()));
    }

    @Test
    void createItemCommentThrowValidationExceptionTest() {
        CommentShort commentShort = new CommentShort("");

        assertThrows(ValidationException.class, () -> service.createComment(1L, 1L, commentShort));
    }

    @Test
    void createItemCommentThrowBookingNotFoundExceptionTest() {
        item.setOwner(user);
        bookingPast.setBooker(user1);
        bookingPast.setItem(item);
        bookingPast.setStatus(BookingStatus.APPROVED);

        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));

        when(userRepository.findById(2L))
                .thenReturn(Optional.ofNullable(user1));

        when(repository.findById(1L))
                .thenReturn(Optional.ofNullable(item));

        when(commentRepository.save(any()))
                .thenReturn(comment);

        assertThrows(NotFoundException.class, () -> service.createComment(user1.getId(), 1L, commentShort));
    }

    @Test
    void createItemCommentThrowBadUserIdExceptionTest() {
        item.setOwner(user);
        bookingPast.setBooker(user1);
        bookingPast.setItem(item);
        bookingPast.setStatus(BookingStatus.APPROVED);
        item1.setOwner(user2);
        bookingVeryPast.setBooker(user);
        bookingVeryPast.setItem(item1);
        bookingVeryPast.setStatus(BookingStatus.APPROVED);

        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));

        when(userRepository.findById(2L))
                .thenReturn(Optional.ofNullable(user1));

        when(repository.findById(1L))
                .thenReturn(Optional.ofNullable(item));

        when(repository.findById(2L))
                .thenReturn(Optional.ofNullable(item1));

        when(bookingRepository.findFirstByBooker_IdAndEndAfterOrderByStartDesc(anyLong(), any()))
                .thenReturn(bookingVeryPast);

        assertThrows(ValidationException.class, () -> service.createComment(user1.getId(), 2L, commentShort));
    }

    @Test
    void createItemCommentThrowInvalidStatusExceptionTest() {
        item.setOwner(user);
        bookingPast.setBooker(user1);
        bookingPast.setItem(item);
        bookingPast.setStatus(BookingStatus.REJECTED);

        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));

        when(userRepository.findById(2L))
                .thenReturn(Optional.ofNullable(user1));

        when(repository.findById(1L))
                .thenReturn(Optional.ofNullable(item));

        when(bookingRepository.findFirstByBooker_IdAndEndAfterOrderByStartDesc(anyLong(), any()))
                .thenReturn(bookingPast);

        when(commentRepository.save(any()))
                .thenReturn(comment);

        assertThrows(ValidationException.class, () -> service.createComment(user1.getId(), 1L, commentShort));
    }

    @Test
    void createItemCommentThrowBookingNotStartedExceptionTest() {
        item.setOwner(user);
        bookingFuture.setBooker(user1);
        bookingFuture.setItem(item);
        bookingFuture.setStatus(BookingStatus.APPROVED);

        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));

        when(userRepository.findById(2L))
                .thenReturn(Optional.ofNullable(user1));

        when(repository.findById(1L))
                .thenReturn(Optional.ofNullable(item));

        when(bookingRepository.findFirstByBooker_IdAndEndAfterOrderByStartDesc(anyLong(), any()))
                .thenReturn(bookingFuture);

        when(commentRepository.save(any()))
                .thenReturn(comment);

        assertThrows(ValidationException.class, () -> service.createComment(user1.getId(), 1L, commentShort));
    }

    @Test
    void updateItemTest() {
        item.setOwner(user);
        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));

        when(repository.findById(1L))
                .thenReturn(Optional.ofNullable(item));

        service.updateItem(user.getId(), item1, 1L);

        ItemDto updatedItem = service.getItem(user.getId(), 1L);

        assertThat(updatedItem.getId(), notNullValue());
        assertThat(updatedItem.getName(), equalTo(item1.getName()));
        assertThat(updatedItem.getDescription(), equalTo(item1.getDescription()));
    }

    @Test
    void updateItemThrowUserNotFoundExceptionTest() {
        when(userRepository.findById(any()))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.updateItem(99L, item1, 99L));
    }

    @Test
    void updateItemThrowNoItemExceptionTest() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));

        assertThrows(NotFoundException.class, () -> service.updateItem(1L, item1, 99L));
    }

    @Test
    void updateItemThrowNonExistingItemExceptionTest() {
        item.setOwner(user1);
        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));

        when(repository.findById(1L))
                .thenReturn(Optional.ofNullable(item));

        assertThrows(NotFoundException.class, () -> service.updateItem(user.getId(), item1, 1L));
    }

    @Test
    void updateItemThrowBadUserIdExceptionTest() {
        assertThrows(ValidationException.class, () -> service.updateItem(0L, item1, 1L));
    }

    @Test
    void getItemWithCommentsTest() {
        item.setOwner(user);
        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));

        when(repository.findById(1L))
                .thenReturn(Optional.ofNullable(item));

        when(commentRepository.findAllByItem_Id(any()))
                .thenReturn(List.of(comment));

        ItemDto itemComm = service.getItem(user.getId(), 1L);

        assertThat(itemComm.getId(), notNullValue());
        assertThat(itemComm.getComments(), hasSize(1));
    }

    @Test
    void getItemThrowItemNotFoundExceptionTest() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));

        assertThrows(NotFoundException.class, () -> service.getItem(user.getId(), 99L));
    }

    @Test
    void getItemWithLastBookingAndNextBookingTest() {
        item.setOwner(user);
        bookingFuture.setItem(item);
        bookingFuture.setBooker(user1);
        bookingFuture.setStatus(BookingStatus.APPROVED);
        bookingPast.setBooker(user2);
        bookingPast.setItem(item1);
        bookingPast.setStatus(BookingStatus.APPROVED);

        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));

        when(repository.findById(1L))
                .thenReturn(Optional.ofNullable(item));

        when(repository.findById(2L))
                .thenReturn(Optional.ofNullable(item));

        when(bookingRepository.findFirst2ByItem_IdAndStartBeforeAndStatusOrderByStartDesc(any(), any(), any()))
                .thenReturn(List.of(bookingPast));

        when(bookingRepository.findFirst2ByItem_IdAndStartGreaterThanEqualAndStatusOrderByStartAsc(any(), any(), any()))
                .thenReturn(List.of(bookingFuture));

        ItemDto itemDto = service.getItem(user.getId(), 1L);

        assertThat(itemDto.getLastBooking().getBookerId(), equalTo(user2.getId()));
        assertThat(itemDto.getNextBooking().getBookerId(), equalTo(user1.getId()));
    }

    @Test
    void getItemWithLastBookingTest() {
        item.setOwner(user);
        bookingPast.setBooker(user1);
        bookingPast.setItem(item);
        bookingPast.setStatus(BookingStatus.APPROVED);

        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));

        when(repository.findById(1L))
                .thenReturn(Optional.ofNullable(item));

        when(bookingRepository.findFirst2ByItem_IdAndStartBeforeAndStatusOrderByStartDesc(any(), any(), any()))
                .thenReturn(List.of(bookingPast));

        when(bookingRepository.findFirst2ByItem_IdAndStartGreaterThanEqualAndStatusOrderByStartAsc(any(), any(), any()))
                .thenReturn(new ArrayList<>());

        ItemDto itemDto = service.getItem(user.getId(), 1L);

        assertThat(itemDto.getLastBooking().getBookerId(), equalTo(user1.getId()));
        assertThat(itemDto.getNextBooking(), nullValue());
    }

    @Test
    void getItemWithNextBookingTest() {
        item.setOwner(user);
        bookingFuture.setItem(item);
        bookingFuture.setBooker(user1);
        bookingFuture.setStatus(BookingStatus.APPROVED);

        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));

        when(repository.findById(1L))
                .thenReturn(Optional.ofNullable(item));

        when(bookingRepository.findFirst2ByItem_IdAndStartBeforeAndStatusOrderByStartDesc(any(), any(), any()))
                .thenReturn(new ArrayList<>());

        when(bookingRepository.findFirst2ByItem_IdAndStartGreaterThanEqualAndStatusOrderByStartAsc(any(), any(), any()))
                .thenReturn(List.of(bookingFuture));

        ItemDto itemDto = service.getItem(user.getId(), 1L);

        assertThat(itemDto.getNextBooking().getBookerId(), equalTo(user1.getId()));
        assertThat(itemDto.getLastBooking(), nullValue());
    }

    @Test
    void getItemsWithoutFromThrowValidationExceptionTest() {
        assertThrows(ValidationException.class, () -> service.getItems(99L, -1, 2));
    }

    @Test
    void getItemsWithoutSizeThrowValidationExceptionTest() {
        assertThrows(ValidationException.class, () -> service.getItems(99L, 0, -1));
    }

    @Test
    void getItemsTest() {
        item.setOwner(user);
        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));

        when(repository.findAllByOwner_Id(any(), any()))
                .thenReturn(List.of(item));

        List<ItemDto> itemDtos = service.getItems(user.getId(), 0, 20);

        assertThat(itemDtos, hasSize(1));
        for (ItemDto itemDto : itemDtos) {
            assertThat(itemDto, allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(item.getName())),
                    hasProperty("description", equalTo(item.getDescription()))
            ));
        }
    }

    @Test
    void getItemsWithCommentsTest() {
        item.setOwner(user);
        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));

        when(repository.findAllByOwner_Id(any(), any()))
                .thenReturn(List.of(item));

        when(commentRepository.findAllByItem_Owner_Id(any()))
                .thenReturn(List.of(List.of(comment)));

        List<ItemDto> itemDtos = service.getItems(user.getId(), 0, 20);

        assertThat(itemDtos, hasSize(1));
        for (ItemDto itemDto : itemDtos) {
            assertThat(itemDto, allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(item.getName())),
                    hasProperty("description", equalTo(item.getDescription())),
                    hasProperty("comments", hasSize(1))
            ));
        }
    }

    @Test
    void getItemsWitLastBookingAndNextBookingTest() {
        item.setOwner(user);
        item1.setOwner(user);
        bookingFuture.setItem(item);
        bookingFuture.setBooker(user1);
        bookingFuture.setStatus(BookingStatus.APPROVED);
        bookingPast.setItem(item1);
        bookingPast.setBooker(user2);
        bookingPast.setStatus(BookingStatus.APPROVED);

        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));

        when(repository.findAllByOwner_Id(any(), any()))
                .thenReturn(List.of(item));

        when(bookingRepository.findAllByItem_Owner_IdAndStartBeforeAndStatusOrderByStartDesc(any(), any(), any()))
                .thenReturn(List.of(bookingPast));

        when(bookingRepository.findAllByItem_Owner_IdAndStartAfterAndStatusOrderByStartAsc(any(), any(), any()))
                .thenReturn(List.of(bookingFuture));


        List<ItemDto> itemDtos = service.getItems(user.getId(), 0, 20);

        assertThat(itemDtos, hasSize(2));
    }

    @Test
    void getItemsWithLastBookingAndNextBookingWithLastMoreTest() {
        item.setOwner(user);
        item1.setOwner(user);
        bookingFuture.setItem(item);
        bookingFuture.setBooker(user1);
        bookingFuture.setStatus(BookingStatus.APPROVED);
        bookingPast.setItem(item1);
        bookingPast.setBooker(user2);
        bookingPast.setStatus(BookingStatus.APPROVED);
        bookingVeryPast.setStatus(BookingStatus.APPROVED);

        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));

        when(repository.findAllByOwner_Id(any(), any()))
                .thenReturn(List.of(item));

        when(bookingRepository.findAllByItem_Owner_IdAndStartBeforeAndStatusOrderByStartDesc(any(), any(), any()))
                .thenReturn(List.of(bookingPast, bookingVeryPast));

        when(bookingRepository.findAllByItem_Owner_IdAndStartAfterAndStatusOrderByStartAsc(any(), any(), any()))
                .thenReturn(List.of(bookingFuture));


        assertThrows(UnsupportedOperationException.class, () -> service.getItems(user.getId(), 0, 20));

    }

    @Test
    void getItemsWithLastBookingAndNextBookingWithNextMoreTest() {
        item.setOwner(user);
        item1.setOwner(user);
        bookingFuture.setItem(item);
        bookingFuture.setBooker(user1);
        bookingFuture.setStatus(BookingStatus.APPROVED);
        bookingPast.setItem(item1);
        bookingPast.setBooker(user2);
        bookingPast.setStatus(BookingStatus.APPROVED);
        bookingVeryPast.setStatus(BookingStatus.APPROVED);

        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));

        when(repository.findAllByOwner_Id(any(), any()))
                .thenReturn(List.of(item));

        when(bookingRepository.findAllByItem_Owner_IdAndStartBeforeAndStatusOrderByStartDesc(any(), any(), any()))
                .thenReturn(List.of(bookingPast));

        when(bookingRepository.findAllByItem_Owner_IdAndStartAfterAndStatusOrderByStartAsc(any(), any(), any()))
                .thenReturn(List.of(bookingFuture, bookingVeryPast));


        assertThrows(UnsupportedOperationException.class, () -> service.getItems(user.getId(), 0, 20));
    }

    @Test
    void getItemsWithLastBookingAndNextBookingAndCommentsTest() {
        item.setOwner(user);
        item1.setOwner(user);
        bookingFuture.setItem(item);
        bookingFuture.setBooker(user1);
        bookingFuture.setStatus(BookingStatus.APPROVED);
        bookingPast.setItem(item1);
        bookingPast.setBooker(user2);
        bookingPast.setStatus(BookingStatus.APPROVED);

        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));

        when(repository.findAllByOwner_Id(any(), any()))
                .thenReturn(List.of(item));

        when(commentRepository.findAllByItem_Owner_Id(any()))
                .thenReturn(List.of(List.of(comment)));

        when(bookingRepository.findAllByItem_Owner_IdAndStartBeforeAndStatusOrderByStartDesc(any(), any(), any()))
                .thenReturn(List.of(bookingPast));

        when(bookingRepository.findAllByItem_Owner_IdAndStartAfterAndStatusOrderByStartAsc(any(), any(), any()))
                .thenReturn(List.of(bookingFuture));


        List<ItemDto> itemDtos = service.getItems(user.getId(), 0, 20);

        assertThat(itemDtos, hasSize(2));
    }

    @Test
    void getItemsSearchWithoutFromThrowValidationExceptionTest() {
        assertThrows(ValidationException.class, () -> service.getItemSearch(99L, null, -1, 10));
    }

    @Test
    void getItemsSearchWithoutSizeThrowValidationExceptionTest() {
        assertThrows(ValidationException.class, () -> service.getItemSearch(99L, null, 0, -1));
    }

    @Test
    void getItemsSearchEmptyListTest() {
        when(repository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(any(), any(), any()))
                .thenReturn(new ArrayList<>());

        List<Item> items = service.getItemSearch(1L, "", 0, 20);

        assertThat(items, hasSize(0));
    }

    @Test
    void getItemsSearchTest() {
        when(repository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(any(), any(), any()))
                .thenReturn(List.of(item));

        List<Item> items = service.getItemSearch(1L, "name", 0, 20);

        assertThat(items, hasSize(1));
        for (Item item2 : items) {
            assertThat(item2, allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(item.getName())),
                    hasProperty("description", equalTo(item.getDescription()))
            ));
        }
    }
}
