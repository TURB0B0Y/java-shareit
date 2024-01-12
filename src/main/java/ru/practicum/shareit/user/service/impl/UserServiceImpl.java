package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.APIConflictException;
import ru.practicum.shareit.exception.APINotFoundException;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public User createUser(CreateUserDto dto) {
        System.out.println("aaaa1");
        checkEmailIsUsed(dto.getEmail());
        User user = UserMapper.mapToMode(dto);
        System.out.println("aaaa2");
        return userStorage.save(user);
    }

    @Override
    public User updateUser(UserDto dto) {
        User user = getUserById(dto.getId());
        if (Objects.nonNull(dto.getEmail()) && !dto.getEmail().equalsIgnoreCase(user.getEmail())) {
            checkEmailIsUsed(dto.getEmail());
            user.setEmail(dto.getEmail());
        }
        if (Objects.nonNull(dto.getName())) {
            user.setName(dto.getName());
        }
        return userStorage.save(user);
    }

    private void checkEmailIsUsed(String email) {
        if (userStorage.existsByEmail(email)) {
            throw new APIConflictException("Почта %s уже используется", email);
        }
    }

    @Override
    public User getUserById(int userId) {
        return userStorage.findById(userId)
                .orElseThrow(() -> new APINotFoundException("Пользователь id %s не найден", userId));
    }

    @Override
    public void deleteUserById(int userId) {
        userStorage.deleteById(userId);
    }

    @Override
    public Collection<User> getAll() {
        return userStorage.getAll();
    }

}
