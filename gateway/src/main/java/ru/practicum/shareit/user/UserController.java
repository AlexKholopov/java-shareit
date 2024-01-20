package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.utils.Marker;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Validated(Marker.OnCreate.class) UserDto user) {
        log.info("Requested creating user");
        return userClient.createUser(user);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@RequestBody @Validated(Marker.OnUpdate.class) UserDto user, @PathVariable long userId) {
        log.info("Requested updating user with id {}", userId);
        return userClient.updateUser(user, userId);
    }

    @DeleteMapping("{userId}")
    public ResponseEntity<Object> deleteUserById(@PathVariable long userId) {
        log.info("Requested deleting user with id {}", userId);
        return userClient.deleteUserById(userId);
    }

    @GetMapping("{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable long userId) {
        log.info("Requested user with id {}", userId);
        return userClient.getUserById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("Requested all users");
        return userClient.getUsers();
    }

}
