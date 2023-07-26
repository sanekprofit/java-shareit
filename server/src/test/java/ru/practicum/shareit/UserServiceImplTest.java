package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:shareit", webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplTest {

    private final UserService service;
    private final EntityManager em;

    @Test
    void saveUserTest() {
        UserDto userDto = new UserDto("name", "email@email.com");

        service.createUser(userDto);

        TypedQuery<User> query = em.createQuery("SELECT u FROM User u", User.class);
        User user = query.getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void getUserTest() {
        UserDto userDto = new UserDto("name", "email@email.com");

        User entity = UserMapper.toUser(userDto);
        em.persist(entity);
        em.flush();

        User user = service.getUser(entity.getId());

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void getUserThrowNotFoundExceptionTest() {
        assertThrows(NotFoundException.class, () -> service.getUser(99L));
    }

    @Test
    void getUsersTest() {
        List<UserDto> sourceUsers = List.of(
                new UserDto("name", "email@email.com"),
                new UserDto("anotherName", "email@gmail.com")
        );

        for (UserDto user : sourceUsers) {
            User entity = UserMapper.toUser(user);
            em.persist(entity);
        }
        em.flush();

        List<User> targetUsers = service.getUsers();

        assertThat(targetUsers, hasSize(sourceUsers.size()));
        for (UserDto sourceUser : sourceUsers) {
            assertThat(targetUsers, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(sourceUser.getName())),
                    hasProperty("email", equalTo(sourceUser.getEmail()))
            )));
        }
    }

    @Test
    void patchUserWithoutEmailTest() {
        UserDto userDto = new UserDto("name", "email@email.com");

        User entity = UserMapper.toUser(userDto);
        em.persist(entity);

        User patchUser = new User("updatedName", null);

        service.patchUser(patchUser, entity.getId());

        TypedQuery<User> query1 = em.createQuery("SELECT u FROM User u WHERE u.name = :name", User.class);
        User user = query1.setParameter("name", patchUser.getName())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(patchUser.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void patchUserWithoutNameTest() {
        UserDto userDto = new UserDto("name", "email@email.com");

        User entity = UserMapper.toUser(userDto);
        em.persist(entity);

        User patchUser = new User(null, "update@email.com");

        service.patchUser(patchUser, entity.getId());

        TypedQuery<User> query1 = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
        User user = query1.setParameter("email", patchUser.getEmail())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(patchUser.getEmail()));
    }

    @Test
    void patchUserThrowDuplicateExceptionTest() {
        UserDto userDto = new UserDto("name", "email@email.com");
        UserDto userDto1 = new UserDto("newName", "emailNew@email.com");

        User entity = UserMapper.toUser(userDto);
        User entity1 = UserMapper.toUser(userDto1);
        em.persist(entity);
        em.persist(entity1);

        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.name = :name", User.class);
        User savedUser = query.setParameter("name", userDto.getName())
                .getSingleResult();

        User patchUser = new User(null, "emailNew@email.com");

        assertThrows(DuplicateException.class, () -> service.patchUser(patchUser, savedUser.getId()));
    }

    @Test
    void patchUserThrowNotFoundExceptionTest() {
        User patchUser = new User("updatedName", "emailNew@email.com");
        assertThrows(NotFoundException.class, () -> service.patchUser(patchUser, 99L));
    }

    @Test
    void deleteUserTest() {
        UserDto userDto = new UserDto("name", "email@email.com");

        User entity = UserMapper.toUser(userDto);
        em.persist(entity);
        em.flush();

        service.deleteUser(entity.getId());

        TypedQuery<User> query = em.createQuery("SELECT u FROM User u", User.class);
        List<User> noUsersList = query.getResultList();

        assertThat(noUsersList, hasSize(0));
    }

    @Test
    void deleteUserThrowNotFoundExceptionTest() {
        assertThrows(NotFoundException.class, () -> service.deleteUser(99L));
    }
}
