package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.User;

public class UserMapper {

    public static UserDto toUserDto(User user) {
        return new UserDto(
                User.builder()
                        .name(user.getName())
                        .email(user.getEmail())
                        .build()
        );
    }
}
