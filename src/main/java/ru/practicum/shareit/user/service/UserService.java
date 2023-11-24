package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(User user, long id) {
        if (id < 1) {
            log.error("Wrong user id - {}", user.getId());
            throw new ValidationException("User id must be positive");
        }
        User oldUser = userRepository.findById(id).orElseThrow();
        user.setId(id);
        user.setName(user.getName() == null ? oldUser.getName() : user.getName());
        user.setEmail(user.getEmail() == null ? oldUser.getEmail() : user.getEmail());
        return userRepository.save(user);
    }

    public void deleteUser(long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }

    public User getUserById(long id) {
        Optional<User> maybeUser = userRepository.findById(id);
        if (maybeUser.isPresent()) {
            return maybeUser.get();
        } else {
            throw new NotFoundException("No such user was found");
        }
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }
}
