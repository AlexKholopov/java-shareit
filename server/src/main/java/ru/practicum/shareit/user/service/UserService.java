package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDto createUser(UserDto user) {
        var userToSave = userMapper.fromDto(user);
        var savedUser = userRepository.save(userToSave);
        var res = userMapper.toDto(savedUser);
        return res;
    }

    public UserDto updateUser(UserDto user, long id) {
        if (id < 1) {
            log.error("Wrong user id - {}", id);
            throw new ConflictException("User id must be positive");
        }
        User oldUser = userRepository.findById(id).orElseThrow(() -> new NotFoundException("No such user was found"));
        user.setId(id);
        String name = user.getName();
        if (name == null || name.isBlank()) {
            user.setName(oldUser.getName());
        }
        String email = user.getEmail();
        if (email == null || email.isBlank()) {
            user.setEmail(oldUser.getEmail());
        }
        return userMapper.toDto(userRepository.save(userMapper.fromDto(user)));
    }

    public void deleteUser(long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("No such user was found"));
        userRepository.delete(user);
    }

    public UserDto getUserById(long id) {
        return userMapper.toDto(userRepository.findById(id).orElseThrow(() -> new NotFoundException("No such user was found")));
    }

    public List<UserDto> getUsers() {
        return userRepository.findAll().stream().map(userMapper::toDto).collect(Collectors.toList());
    }
}
