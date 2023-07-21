package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.User;

public class UserMapper {
    public static User toUser(UserDto userDto) {
        return new User(userDto.getName(), userDto.getEmail());
    }
}
