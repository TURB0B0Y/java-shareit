package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.APIConflictException;
import ru.practicum.shareit.exception.APINotFoundException;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.impl.UserServiceImpl;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testDataAnnotation() {

        UserDto userDto = new UserDto(1, "Test", "test@test.com");

        assertEquals(1, userDto.getId());
        assertEquals("Test", userDto.getName());
        assertEquals("test@test.com", userDto.getEmail());

        UserDto userDto2 = new UserDto(1, "Test", "test@test.com");
        assertEquals(userDto, userDto2);
        assertEquals(userDto.hashCode(), userDto2.hashCode());
        assertEquals(userDto.toString(), userDto2.toString());
    }

    @Test
    void addNewUser() {
        int userId = 0;
        User expectedUser = new User(userId, "Test", "test@test.com");
        when(userRepository.save(any()))
                .thenReturn(expectedUser);
        User actualUser = userService.createUser(new CreateUserDto("Test", "test@test.com"));
        assertEquals(expectedUser, actualUser);
    }

    @Test
    void addNewUserDuplicateEmail() {
        CreateUserDto dto = new CreateUserDto("Test", "test@test.com");
        when(userRepository.save(any()))
                .thenThrow(APIConflictException.class);

        assertThrows(APIConflictException.class, () -> userService.createUser(dto));
    }

    @Test
    void getUserById() {
        int userId = 0;
        UserDto dto = new UserDto(userId, "Test", "test@test.com");
        User expectedUser = UserMapper.mapToMode(dto);
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(expectedUser));
        User actualUser = userService.getUserById(userId);
        assertEquals(expectedUser, actualUser);
    }

    @Test
    void getUserByIdNotFound() {
        int userId = 0;
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());
        assertThrows(APINotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    void updateUserByIdNotFound() {
        UserDto dto = new UserDto(0, null, "test@test.com");
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.empty());
        assertThrows(APINotFoundException.class, () -> userService.updateUser(dto));
    }

    @Test
    void updateUserByIdNotFoundNoEmail() {
        UserDto dto = new UserDto(0, "test", null);
        User expectedUser = UserMapper.mapToMode(dto);

        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.empty());
        assertThrows(APINotFoundException.class, () -> userService.updateUser(UserMapper.toDto(expectedUser)));
    }

    @Test
    void getAllUsers() {
        User expectedUser = new User(0, "Test", "test@test.com");
        when(userRepository.findAll())
                .thenReturn(List.of(expectedUser));
        Collection<User> actualUser = userService.getAll();
        assertEquals(List.of(expectedUser), actualUser);
    }

    @Test
    void updateUserByIdNoEmail() {
        int userId = 1;
        User expectedUser = new User(userId, "Test", "test@test.com");
        User noEmailUser = new User(userId, "Test", null);
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(expectedUser));
        when(userRepository.save(any()))
                .thenReturn(expectedUser);
        User actualUser = userService.updateUser(UserMapper.toDto(noEmailUser));
        assertEquals(expectedUser, actualUser);
    }

    @Test
    void updateUserByIdNoName() {
        int userId = 1;
        User expectedUser = new User(userId, "Test", "test@test.com");
        User noEmailUser = new User(userId, null, "test@test.com");
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(expectedUser));
        when(userRepository.save(any()))
                .thenReturn(expectedUser);
        User actualUser = userService.updateUser(UserMapper.toDto(noEmailUser));
        assertEquals(expectedUser, actualUser);
    }

    @Test
    public void testDeleteUser() {
        User expectedUser = new User(1, "Test", "test@test.com");
        userRepository.save(expectedUser);
        int id = expectedUser.getId();

        userService.deleteUserById(id);

        assertFalse(userRepository.existsById(id));
    }

    @Test
    public void testDelete() {
        UserService userServiceMock = mock(UserService.class);

        UserController userController = new UserController(userServiceMock);

        User user = new User();
        user.setId(1);
        user.setName("Test");

        userController.deleteUserById(1);

        verify(userServiceMock, times(1)).deleteUserById(1);
    }
}