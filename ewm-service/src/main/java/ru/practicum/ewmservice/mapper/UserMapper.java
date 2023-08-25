package ru.practicum.ewmservice.mapper;

import ru.practicum.dto.input.NewUserDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.ewmservice.model.User;

public class UserMapper {
    public static User toModel(NewUserDto newUserDto) {
        return User.builder()
                .email(newUserDto.getEmail())
                .name(newUserDto.getName()).build();
    }

    public static User toModel(UserDto userDto) {
        return User.builder()
                .email(userDto.getEmail())
                .name(userDto.getName()).build();
    }

    public static UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName()).build();
    }

    public static UserShortDto toShortDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName()).build();
    }
}
