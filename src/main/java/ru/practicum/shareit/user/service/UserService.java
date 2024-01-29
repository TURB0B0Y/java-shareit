package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {

    User createUser(CreateUserDto dto);

    User updateUser(UserDto dto);

    User getUserById(int userId);

    void deleteUserById(int userId);

    Collection<User> getAll();

}
