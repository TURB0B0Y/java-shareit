package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@SuppressWarnings("unused")
public class UserController {

    private final UserClient userClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createUser(@RequestBody @Valid CreateUserDto dto) {
        log.info("createUser {}", dto);
        return userClient.createUser(dto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable @PositiveOrZero int userId, @RequestBody @Valid UserDto dto) {
        log.info("updateUser {}", dto);
        return userClient.updateUser(userId, dto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable @PositiveOrZero int userId) {
        log.info("getUserById {}", userId);
        return userClient.getUserById(userId);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Object> deleteUserById(@PathVariable @PositiveOrZero int userId) {
        log.info("deleteUserById {}", userId);
        return userClient.deleteUserById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("getAll");
        return userClient.getAll();
    }


}
