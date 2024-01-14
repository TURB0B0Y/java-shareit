package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    User save(User user);

    Optional<User> findById(int userId);

    boolean existsByEmail(String email);

    void deleteById(int userId);

    Collection<User> getAll();

}
