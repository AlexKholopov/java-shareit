package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.model.dto.UserId;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    @InjectMocks
    private UserService userService;

    @Test
    void createUserSuccess() {
        UserDto user = new UserDto();
        User user1 = new User();
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user1);
        assertEquals(user, userService.createUser(user));
    }

    @Test
    void updateUserAllFieldsSuccess() {
        UserDto user = new UserDto();
        user.setEmail("email@email.com");
        user.setName("name");
        user.setId(1L);
        User oldUser = new User();
        var userFromDb = userMapper.fromDto(user);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(oldUser));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(userFromDb);
        var res = userService.updateUser(user, 1L);

        assertEquals(user, res);
    }

    @Test
    void updateUserNoFieldsSuccess() {
        UserDto user = new UserDto();
        user.setId(1L);
        User oldUser = new User();
        var userFromDb = userMapper.fromDto(user);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(oldUser));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(userFromDb);
        var res = userService.updateUser(user, 1L);

        assertEquals(user, res);
    }

    @Test
    void updateUserFailInvalidId() {
        UserDto user = new UserDto();
        var res = assertThrows(ConflictException.class, () -> userService.updateUser(user, -1L));

        assertEquals("User id must be positive", res.getMessage());
    }

    @Test
    void deleteUserSuccess() {
        User user = new User();

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);
        verify(userRepository).delete(user);
    }

    @Test
    void getUserByIdSuccess() {
        User user = new User();

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        var res = userService.getUserById(1L);
        assertEquals(userMapper.toDto(user), res);
    }

    @Test
    void getUsersSuccess() {
        User user = new User();

        Mockito.when(userRepository.findAll()).thenReturn(List.of(user));
        var res = userService.getUsers();
        assertEquals(List.of(userMapper.toDto(user)), res);
    }

    @Test
    void mapperTest() {
        User user = new User();
        user.setId(1L);
        User user1 = new User();
        user.setId(1L);
        UserId userId = new UserId();
        userId.setId(1L);
        var res = userMapper.toId(user);
        assertEquals(userId, res);
        user.equals(user1);
    }
}