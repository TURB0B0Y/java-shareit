package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.APINotFoundException;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public User createUser(CreateUserDto dto) {
        User user = UserMapper.mapToMode(dto);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(UserDto dto) {
        User user = getUserById(dto.getId());
        if (Objects.nonNull(dto.getEmail()) && !dto.getEmail().equalsIgnoreCase(user.getEmail())) {
            user.setEmail(dto.getEmail());
        }
        if (Objects.nonNull(dto.getName())) {
            user.setName(dto.getName());
        }
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new APINotFoundException("Пользователь id %d не найден", userId));
    }

    @Override
    @Transactional
    public void deleteUserById(int userId) {
        userRepository.deleteById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<User> getAll() {
        return userRepository.findAll();
    }

}
