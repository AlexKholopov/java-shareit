package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Repository
public class InMemoryUserRepository implements UserRepository {

    private long idCount = 1;
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> usersEmail = new HashSet<>();

    @Override
    public User createUser(User user) {
        if (usersEmail.contains(user.getEmail())) {
            log.error("Creating user fail, user email {} already exist", user.getEmail());
            throw new ValidationException(String.format("User with email %s already exists", user.getEmail()));
        }
        user.setId(idCount++);
        usersEmail.add(user.getEmail());
        users.put(user.getId(), user);
        log.info("User successfully created");
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            log.error("Updating user fail, user id {} not found", user.getId());
            throw new NotFoundException(String.format("User with id %d not found", user.getId()));
        }

        User user1 = users.get(user.getId());
        if (usersEmail.contains(user.getEmail()) && !user.getEmail().equals(user1.getEmail())) {
            log.error("Updating user fail, user email {} already exist", user.getEmail());
            throw new ValidationException(String.format("User with email %s already exists", user.getEmail()));
        }

        if (user.getEmail() == null) {
            user.setEmail(user1.getEmail());
        }

        if (user.getName() == null) {
            user.setName(user1.getName());
        }
        usersEmail.remove(user1.getEmail());
        users.replace(user.getId(), user);
        usersEmail.add(user.getEmail());
        log.info("User id {} successfully updated", user.getId());
        return user;
    }

    @Override
    public void deleteUser(long id) {
        usersEmail.remove(users.get(id).getEmail());
        users.remove(id);
        log.info("User id {} successfully deleted", id);
    }

    @Override
    public User getUserById(long id) {
        if (!users.containsKey(id)) {
            log.error("User with id {} not found", id);
            throw new NotFoundException(String.format("User with id %s not found", id));
        }
        log.info("Successful user id {} request", id);
        return users.get(id);
    }

    @Override
    public List<User> getUsers() {
        log.info("Successful all users request");
        return new ArrayList<>(users.values());
    }
}
