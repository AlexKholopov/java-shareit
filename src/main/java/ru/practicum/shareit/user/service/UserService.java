package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User createUser(User user) {
        return userRepository.createUser(user);
    }

    public User updateUser(User user, long id) {
        user.setId(id);
        return userRepository.updateUser(user);
    }

    public void deleteUser(long id) {
        userRepository.deleteUser(id);
    }

    public User getUserById(long id) {
        return userRepository.getUserById(id);
    }

    public List<User> getUsers() {
        return userRepository.getUsers();
    }
}
