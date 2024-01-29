package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.APINotFoundException;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.impl.UserServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class UserServiceIntegrationTest {

    private final UserRepository userRepository;
    private final UserServiceImpl userService;


    @Test
    public void testAddUser() {

        CreateUserDto user = new CreateUserDto("John Doe", "john@example.com");
        User addedUser = userService.createUser(user);

        assertNotNull(addedUser);
        assertEquals(user.getName(), addedUser.getName());
        assertEquals(user.getEmail(), addedUser.getEmail());
    }

    @Test
    public void testGetUserById() {
        User user = userRepository.save(new User(1, "John Doe", "john@example.com"));

        User retrievedUser = userService.getUserById(user.getId());

        assertNotNull(retrievedUser);
        assertEquals(user.getId(), retrievedUser.getId());
        assertEquals(user.getName(), retrievedUser.getName());
        assertEquals(user.getEmail(), retrievedUser.getEmail());
    }

    @Test
    public void testGetUserById_ThrowsNotFoundException() {
        assertThrows(APINotFoundException.class, () -> userService.getUserById(9999));
    }

    @Test
    public void testGetAllUsers() {
        User user1 = new User(2, "John Doe", "john@example.com");
        User user2 = new User(3, "Jane Smith", "jane@example.com");

        List<User> users = userRepository.saveAll(List.of(user1, user2));
        assertEquals(2, users.size());
        assertTrue(users.contains(user1));
        assertTrue(users.contains(user2));

    }

    @Test
    @Transactional
    public void testUpdateUser() {
        User user = userService.createUser(new CreateUserDto("John Doe", "john@example.com"));

        User updatedUser = new User(user.getId(), "Updated Name", null);
        User result = userService.updateUser(UserMapper.toDto(updatedUser));

        assertEquals(user.getId(), result.getId());
        assertEquals(updatedUser.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());

    }

    @Test
    public void testDeleteUser() {
        User user = userRepository.save(new User(1, "John Doe", "john@example.com"));
        userService.deleteUserById(user.getId());
        assertFalse(userRepository.existsById(user.getId()));
    }
}