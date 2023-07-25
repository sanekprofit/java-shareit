package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShort;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

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
public class ItemRequestServiceTest {

    private final ItemRequestService service;
    @MockBean
    private final ItemRequestRepository repository;
    @MockBean
    private final UserRepository userRepository;
    @MockBean
    private final ItemRepository itemRepository;
    private User user;
    private Item item;
    private ItemRequestShort itemRequestShort;
    private ItemRequest itemRequest;

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "name", "email@email.com");
        item = new Item(1L, "name", "description", true);
        itemRequestShort = new ItemRequestShort("description");
        itemRequest = new ItemRequest();
    }

    @Test
    void createItemRequestTest() {
        itemRequest.setDescription("description");
        itemRequest.setRequester(user);

        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));

        when(repository.save(any()))
                .thenReturn(itemRequest);

        ItemRequest request = service.createItemRequest(user.getId(), itemRequestShort);

        assertThat(request.getId(), notNullValue());
        assertThat(request.getDescription(), equalTo(itemRequestShort.getDescription()));
        assertThat(request.getRequester(), equalTo(user));
    }

    @Test
    void createItemRequestThrowBadArgumentsExceptionTest() {
        itemRequestShort.setDescription("");

        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));

        assertThrows(ValidationException.class, () -> service.createItemRequest(1L, itemRequestShort));
    }

    @Test
    void getItemRequestsOwnTest() {
        itemRequest.setId(1L);
        itemRequest.setDescription("description");
        itemRequest.setRequester(user);
        item.setRequest(itemRequest);

        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));

        when(repository.findAllByRequester_Id(anyLong()))
                .thenReturn(List.of(itemRequest));

        when(itemRepository.findAllByRequest_Id(anyLong()))
                .thenReturn(List.of(item));

        List<ItemRequestDto> request = service.getItemRequestsOwn(user.getId());

        assertThat(request, hasSize(1));
        for (ItemRequestDto itemRequest : request) {
            assertThat(itemRequest, allOf(
                    hasProperty("items", hasSize(1))
            ));
        }
    }

    @Test
    void getItemRequestsOwnEmptyRequestsTest() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));

        when(repository.findAllByRequester_Id(anyLong()))
                .thenReturn(List.of());

        List<ItemRequestDto> request = service.getItemRequestsOwn(user.getId());

        assertThat(request, hasSize(0));
    }

    @Test
    void getItemRequestsOwnEmptyItemsTest() {
        itemRequest.setId(1L);
        itemRequest.setDescription("description");
        itemRequest.setRequester(user);

        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));

        when(repository.findAllByRequester_Id(anyLong()))
                .thenReturn(List.of(itemRequest));

        when(itemRepository.findAllByRequest_Id(anyLong()))
                .thenReturn(List.of());

        List<ItemRequestDto> request = service.getItemRequestsOwn(user.getId());

        assertThat(request, hasSize(1));
        for (ItemRequestDto itemRequest : request) {
            assertThat(itemRequest, allOf(
                    hasProperty("items", hasSize(0))
            ));
        }
    }

    @Test
    void getItemRequestsTest() {
        itemRequest.setId(1L);
        itemRequest.setDescription("description");
        itemRequest.setRequester(user);
        item.setRequest(itemRequest);

        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));

        when(repository.findAllByRequester_IdNotLikeOrderByCreatedDesc(anyLong(), any()))
                .thenReturn(List.of(itemRequest));

        when(itemRepository.findAllByRequest_Id(anyLong()))
                .thenReturn(List.of(item));

        List<ItemRequestDto> request = service.getItemRequests(1L, 0, 20);

        assertThat(request, hasSize(1));
        for (ItemRequestDto itemRequest : request) {
            assertThat(itemRequest, allOf(
                    hasProperty("items", hasSize(1))
            ));
        }
    }

    @Test
    void getItemRequestsWithoutItemsTest() {
        itemRequest.setId(1L);
        itemRequest.setDescription("description");
        itemRequest.setRequester(user);

        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));

        when(repository.findAllByRequester_IdNotLikeOrderByCreatedDesc(anyLong(), any()))
                .thenReturn(List.of(itemRequest));

        List<ItemRequestDto> request = service.getItemRequests(1L, 0, 20);

        assertThat(request, hasSize(1));
        for (ItemRequestDto itemRequest : request) {
            assertThat(itemRequest, allOf(
                    hasProperty("items", hasSize(0))
            ));
        }
    }

    @Test
    void getItemRequestsWithoutRequestsTest() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));

        when(repository.findAllByRequester_IdNotLikeOrderByCreatedDesc(anyLong(), any()))
                .thenReturn(List.of());

        List<ItemRequestDto> request = service.getItemRequests(1L, 0, 20);

        assertThat(request, hasSize(0));
    }

    @Test
    void getItemRequestTest() {
        itemRequest.setId(1L);
        itemRequest.setDescription("description");
        itemRequest.setRequester(user);
        item.setRequest(itemRequest);

        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));

        when(repository.findFirstById(anyLong()))
                .thenReturn(itemRequest);

        when(itemRepository.findFirstByRequest_Id(anyLong()))
                .thenReturn(item);

        ItemRequestDto request = service.getItemRequest(user.getId(), 1L);

        assertThat(request.getId(), equalTo(itemRequest.getId()));
        assertThat(request.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(request.getItems(), hasSize(1));
    }

    @Test
    void getItemRequestWithoutItemsTest() {
        itemRequest.setId(1L);
        itemRequest.setDescription("description");
        itemRequest.setRequester(user);
        item.setRequest(itemRequest);

        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));

        when(repository.findFirstById(anyLong()))
                .thenReturn(itemRequest);

        ItemRequestDto request = service.getItemRequest(user.getId(), 1L);

        assertThat(request.getId(), equalTo(itemRequest.getId()));
        assertThat(request.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(request.getItems(), hasSize(0));
    }

    @Test
    void getItemRequestWithoutRequestThrowNotFoundExceptionTest() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));

        when(repository.findFirstById(anyLong()))
                .thenReturn(null);

        assertThrows(NotFoundException.class, () -> service.getItemRequest(1L, 99L));
    }
}
