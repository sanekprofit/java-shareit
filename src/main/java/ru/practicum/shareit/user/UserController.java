package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable("userId") Long userId) {
        log.info("Получен запрос на получение пользователя с id: {}", userId);
        return userService.getUser(userId);
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Получен запрос на получение всех пользователей.");
        return userService.getUsers();
    }

    @PostMapping
    public User createUser(@RequestBody UserDto user) {
        log.info("Получен запрос на создание пользователя: {}", user);
        return userService.createUser(user);
    }

    @PatchMapping("/{userId}")
    public User patchUser(@RequestBody User user,
                          @PathVariable("userId") Long userId) {
        log.info("Получен запрос на обновление пользователя: {}", user);
        return userService.patchUser(user, userId);
    }

    @DeleteMapping("/{userId}")
    public HttpStatus deleteUser(@PathVariable("userId") Long userId) {
        log.info("Получен запрос на удаление пользователя: {}", userId);
        return userService.deleteUser(userId);
    }
}