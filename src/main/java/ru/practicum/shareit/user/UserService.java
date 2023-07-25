package ru.practicum.shareit.user;

import org.springframework.http.HttpStatus;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    User getUser(Long userId);

    List<User> getUsers();

    User createUser(UserDto user);

    User patchUser(User user, Long userId);

    HttpStatus deleteUser(Long userId);
}