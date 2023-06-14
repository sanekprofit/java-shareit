package ru.practicum.shareit.user;

import org.springframework.http.HttpStatus;

import java.util.List;

public interface UserService {
    User getUser(Long userId);

    List<User> getUsers();

    User createUser(User user);

    User patchUser(User user, Long userId);

    HttpStatus deleteUser(Long userId);
}