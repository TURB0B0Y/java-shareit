package ru.practicum.shareit.user.storage.impl;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
@SuppressWarnings("unused")
public class InMemoryUserStorage implements UserStorage {

    private final ConcurrentHashMap<Integer, User> users = new ConcurrentHashMap<>();
    private final AtomicInteger index = new AtomicInteger();

    @Override
    public User save(User user) {
        if (Objects.isNull(user.getId())) {
            user.setId(index.incrementAndGet());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(int userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public boolean existsByEmail(String email) {
        return users.values().stream().anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }

    @Override
    public void deleteById(int userId) {
        users.remove(userId);
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

}
