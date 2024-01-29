package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.APINotFoundException;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase()
class ShareItTests {

	private final UserRepository userRepository;
	private final UserService userService;

	@Test
	void updateUser() {
		userRepository.save(new User(1, "Test", "test@test.com"));
		User newUser = new User(1, "Updated", "test@test.com");
		userService.updateUser(UserMapper.toDto(newUser));
		Assertions.assertEquals(userService.getUserById(1), newUser);
	}

	@Test
	void deleteUser() {
		User user = userService.createUser(new CreateUserDto("Test2", "test2@test.com"));
		userService.deleteUserById(user.getId());
		Assertions.assertThrows(APINotFoundException.class, () -> userService.getUserById(user.getId()));
	}
}