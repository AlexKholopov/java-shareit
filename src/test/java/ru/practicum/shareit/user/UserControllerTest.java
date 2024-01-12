package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;

    @Test
    void createUser() {
        UserDto expectedUser = new UserDto();
        Mockito.when(userService.createUser(expectedUser)).thenReturn(expectedUser);

        UserDto user = userController.createUser(expectedUser);

        assertEquals(expectedUser, user);
    }

    @Test
    void updateUser() {
        UserDto expectedUser = new UserDto();
        Mockito.when(userService.updateUser(expectedUser, 1L)).thenReturn(expectedUser);

        UserDto user = userController.updateUser(expectedUser, 1L);

        assertEquals(expectedUser, user);
    }

    @Test
    void getUserById() {
        UserDto expectedUser = new UserDto();
        Mockito.when(userService.getUserById(Mockito.anyLong())).thenReturn(expectedUser);

        var user = userController.getUserById(1L);

        assertEquals(expectedUser, user);
    }

    @Test
    void getUsersTest() {
        List<UserDto> expectedUsers = List.of(new UserDto());
        Mockito.when(userService.getUsers()).thenReturn(expectedUsers);

        var response = userController.getUsers();

        assertEquals(expectedUsers, response);
    }
}