package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:shareit", webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureMockMvc
public class UserControllerTest {
    @MockBean
    private final UserService userService;
    @Autowired
    ObjectMapper mapper;

    @Test
    void saveUserTest(@Autowired MockMvc mvc) throws Exception {
        User user = new User( "name", "email@email.com");
        when(userService.createUser(any()))
                .thenReturn(user);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));
    }

    @Test
    void saveUserThrowValidationExceptionTest(@Autowired MockMvc mvc) throws Exception {
        User user = new User("name", "invalidEmail");
        when(userService.createUser(any()))
                .thenThrow(ValidationException.class);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUsersTest(@Autowired MockMvc mvc) throws Exception {
        User user = new User("name", "email@email.com");
        when(userService.createUser(any()))
                .thenReturn(user);

        when(userService.getUsers())
                .thenReturn(List.of(user));

        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(user.getName())))
                .andExpect(jsonPath("$[0].email", is(user.getEmail())));
    }

    @Test
    void getUserTest(@Autowired MockMvc mvc) throws Exception {
        User user = new User("name", "email@email.com");
        when(userService.createUser(any()))
                .thenReturn(user);

        when(userService.getUser(any()))
                .thenReturn(user);

        mvc.perform(get("/users/{userId}", user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));
    }

    @Test
    void getUserThrowNotFoundExceptionTest(@Autowired MockMvc mvc) throws Exception {
        User user = new User("name", "email@email.com");
        when(userService.createUser(any()))
                .thenReturn(user);

        when(userService.getUser(99L))
                .thenThrow(NotFoundException.class);

        mvc.perform(get("/users/{userId}", 99L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void patchUserTest(@Autowired MockMvc mvc) throws Exception {
        User user = new User(1L, "name", "email@email.com");
        when(userService.createUser(any()))
                .thenReturn(user);

        User patchUser = new User("updatedName", null);
        when(userService.patchUser(patchUser, user.getId()))
                .thenReturn(patchUser);

        mvc.perform(patch("/users/{userId}", user.getId())
                        .content(mapper.writeValueAsString(patchUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(patchUser.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(patchUser.getName())))
                .andExpect(jsonPath("$.email", is(patchUser.getEmail())));
    }

    @Test
    void patchUserThrowDuplicateExceptionTest(@Autowired MockMvc mvc) throws Exception {
        User user = new User(1L, "name", "email@email.com");
        when(userService.createUser(any()))
                .thenReturn(user);

        User conflictUser = new User(2L, "updatedName", "updated@email.com");
        when(userService.createUser(any()))
                .thenReturn(conflictUser);

        User patchUser = new User("nonUpdated", "updated@email.com");
        when(userService.patchUser(patchUser, user.getId()))
                .thenThrow(DuplicateException.class);

        mvc.perform(patch("/users/{userId}", user.getId())
                        .content(mapper.writeValueAsString(patchUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void deleteUserTest(@Autowired MockMvc mvc) throws Exception {
        User user = new User("name", "email@email.com");
        when(userService.createUser(any()))
                .thenReturn(user);

        when(userService.deleteUser(any()))
                .thenReturn(HttpStatus.OK);

        mvc.perform(delete("/users/{userId}", user.getId())
                    .characterEncoding(StandardCharsets.UTF_8)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
