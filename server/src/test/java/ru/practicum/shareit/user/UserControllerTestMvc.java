package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WebMvcTest(UserController.class)
class UserControllerTestMvc {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;

    @SneakyThrows
    @Test
    void createUserTestSuccess() {
        UserDto user = new UserDto();
        user.setName("Name");
        user.setEmail("mail@mail.ru");
        Mockito.when(userService.createUser(user)).thenReturn(user);

        var res = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(user), res);
    }
/*
    @SneakyThrows
    @Test
    void createUserTestValidationFail() {
        UserDto user = new UserDto();
        Mockito.when(userService.createUser(user)).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        Mockito.verify(userService, Mockito.never()).createUser(user);
    }

  /*  @SneakyThrows
    @Test
    void updateUserFailValidation() {
        long userId = 1L;
        UserDto userInput = new UserDto();
        userInput.setEmail("notValidEmail");

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userInput)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        Mockito.verify(userService, Mockito.never()).updateUser(userInput, userId);
    }

 */

    @SneakyThrows
    @Test
    void getUserByIdTestSuccess() {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", 2L))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(userService).getUserById(2L);
    }

    @SneakyThrows
    @Test
    void getUsersTestSuccess() {
        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(userService).getUsers();
    }
}