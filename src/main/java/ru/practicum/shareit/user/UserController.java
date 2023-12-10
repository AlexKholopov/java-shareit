package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utils.Marker;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController
@Validated
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Validated(Marker.OnCreate.class)
    public UserDto createUser(@Valid @RequestBody UserDto user) {
        log.info("Requested creating user");
        return userService.createUser(user);
    }

    @PatchMapping("/{userId}")
    @Validated(Marker.OnUpdate.class)
    public UserDto updateUser(@RequestBody UserDto user, @PathVariable long userId) {
        log.info("Requested updating user with id {}", userId);
        return userService.updateUser(user, userId);
    }

    @DeleteMapping("{userId}")
    public void deleteUserById(@PathVariable long userId) {
        log.info("Requested deleting user with id {}", userId);
        userService.deleteUser(userId);
    }

    @GetMapping("{userId}")
    public UserDto getUserById(@PathVariable long userId) {
        log.info("Requested user with id {}", userId);
        return userService.getUserById(userId);
    }

    @GetMapping
    public List<UserDto> getUsers() {
        log.info("Requested all users");
        return userService.getUsers();
    }
}
