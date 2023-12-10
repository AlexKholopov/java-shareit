package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
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
        return userMapper.toDto(userRepository.save(userMapper.fromDto(user)));
    }

    public UserDto updateUser(UserDto user, long id) {
        if (id < 1) {
            log.error("Wrong user id - {}", user.getId());
            throw new ValidationException("User id must be positive");
        }
        User oldUser = userRepository.findById(id).orElseThrow(() -> new NotFoundException("No such user was found"));
        user.setId(id);
        if (user.getName() == null || user.getName().isBlank()) user.setName(oldUser.getName());
        else user.setName(user.getName());
        if (user.getEmail() == null || user.getEmail().isBlank()) user.setEmail(oldUser.getEmail());
        else user.setEmail(user.getEmail());
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
