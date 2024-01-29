package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@SuppressWarnings("unused")
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody CreateUserDto dto) {
        log.info("createUser {}", dto);
        User user = userService.createUser(dto);
        return UserMapper.toDto(user);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable int userId, @RequestBody UserDto dto) {
        log.info("updateUser {}", dto);
        dto.setId(userId);
        User user = userService.updateUser(dto);
        return UserMapper.toDto(user);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable int userId) {
        log.info("getUserById {}", userId);
        User user = userService.getUserById(userId);
        return UserMapper.toDto(user);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable int userId) {
        log.info("deleteUserById {}", userId);
        userService.deleteUserById(userId);
    }

    @GetMapping
    public List<UserDto> getAll() {
        Collection<User> users = userService.getAll();
        log.info("getAll");
        return users.stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }


}
