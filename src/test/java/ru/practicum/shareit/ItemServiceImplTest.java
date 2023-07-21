package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemShort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:shareit", webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplTest {

    private final ItemService service;
    private final BookingService bookingService;
    private final ItemRequestService itemRequestService;
    private final UserService userService;
    private final EntityManager em;

    @Test
    void createItemTest() {
        UserDto userDto = new UserDto("name", "email@email.com");
        ItemShort itemShort = new ItemShort("name", "description", true, 0L);

        userService.createUser(userDto);
        service.createItem(1L, itemShort);

        TypedQuery<Item> query = em.createQuery("SELECT i FROM Item i", Item.class);
        Item item = query.getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemShort.getName()));
        assertThat(item.getDescription(), equalTo(itemShort.getDescription()));
    }

    @Test
    void createItemThrowNotFoundExceptionTest() {
        ItemShort itemShort = new ItemShort("name", "description", true, 0L);

        assertThrows(NotFoundException.class, () -> service.createItem(99L, itemShort));
    }


}
